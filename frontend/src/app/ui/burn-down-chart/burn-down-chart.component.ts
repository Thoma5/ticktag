import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { LocalStorageService } from 'ng2-webstorage';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import * as imm from 'immutable';
import { ApiCallService } from '../../service/api-call/api-call.service';
import { TicketApi } from '../../api/api/TicketApi';
import { TickettagApi } from '../../api/api/TickettagApi';
import { TicketeventApi } from '../../api/api/TicketeventApi';
import { ProjectApi } from '../../api/api/ProjectApi';
import { TicketEventResultJson } from '../../api/model/TicketEventResultJson';
import { TicketStoryPointResultJson } from '../../api/model/TicketStoryPointResultJson';
import { TicketTagResultJson } from '../../api/model/TicketTagResultJson';
import { UserResultJson } from '../../api/model/UserResultJson';
import { TicketOverviewTag, TicketOverviewUser } from '../ticket-overview/ticket-overview';
import { TicketFilter } from '../ticket-overview/ticket-filter/ticket-filter';
import { idListToMap } from '../../util/listmaputils';

@Component({
    selector: 'tt-burn-down-chart',
    templateUrl: './burn-down-chart.component.html',
    styleUrls: ['./burn-down-chart.component.scss']
})
export class BurnDownChartComponent implements OnInit {
    private allTicketTagsForFilter: imm.Map<string, TicketOverviewTag>;
    private allProjectUsers: imm.Map<string, TicketOverviewUser>;
    private ticketFilter: TicketFilter = new TicketFilter(undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, true, undefined);
    private tickets = new Map<string, TicketStoryPointResultJson>();
    private projectId: string;
    private FROM_KEY = 'BURNDOWN_FROM';
    private TO_KEY = 'BURNDOWN_TO';
    private FILTER_KEY = 'BURNDOWN_FILTER';
    public toDate = new Date();
    public fromDate = new Date();
    public query = '';
    private idealData: number[] = [];
    private actualData: number[] = [];
    private startData = 0;
    private cachedResult: TicketEventResultJson[] = [];
    public disabledFilterHelper: string = '# status sp dueDate progress';
    public datePickerOpts = {
        autoclose: true,
        todayBtn: 'linked',
        todayHighlight: true,
        assumeNearbyYear: true,
        format: 'yyyy-mm-dd'
    };

    public datePickerToOpts: any;

    //  lineChart
    public lineChartData: Array<any> = [
        { data: [], label: 'Actual Story Points Remaining' },
        { data: [], label: 'Ideal Story Points Remaining' },
    ];
    public lineChartLabels: Array<any> = [];
    public lineChartOptions: any = {
        animation: false,
        responsive: true,
        scales: {
            xAxes: [{
                scaleLabel: {
                    display: true,
                    labelString: 'Iteration Timeline (days)'
                }
            }],
            yAxes: [{
                scaleLabel: {
                    display: true,
                    labelString: 'Story Points'
                }
            }]
        },
        elements: {
            line: {
                tension: 0
            }
        }
    };
    public lineChartColors: Array<any> = [
        { //  red
            backgroundColor: 'rgba(0,0,0,0)',
            borderColor: 'rgba(229, 107, 107, 1)',
            pointBackgroundColor: 'rgba(229, 107, 107, 1)',
            pointBorderColor: 'rgba(229, 107, 107, 1)',
            pointHoverBackgroundColor: 'rgba(229, 107, 107, 1)',
            pointHoverBorderColor: 'rgba(229, 107, 107, 1)'
        },
        { //  green
            backgroundColor: 'rgba(0,0,0,0)',
            borderColor: 'rgba(159, 221, 150, 1)',
            pointBackgroundColor: 'rgba(159, 221, 150, 1)',
            pointBorderColor: 'rgba(159, 221, 150, 1)',
            pointHoverBackgroundColor: 'rgba(159, 221, 150, 1)',
            pointHoverBorderColor: 'rgba(159, 221, 150, 1)'
        },
    ];
    public lineChartLegend: boolean = true;
    public lineChartType: string = 'line';

    private loading = true;
    constructor(private route: ActivatedRoute,
        private router: Router,
        private apiCallService: ApiCallService,
        private ticketEventApi: TicketeventApi,
        private ticketTagsApi: TickettagApi,
        private ticketApi: TicketApi,
        private projectApi: ProjectApi,
        private localStorageService: LocalStorageService
    ) {
        // Clone Options
        this.datePickerToOpts = $.extend({}, this.datePickerOpts);
    }

    public ngOnInit(): void {
        if (this.localStorageService.retrieve(this.FROM_KEY) !== undefined) {
            this.fromDate = new Date(this.localStorageService.retrieve(this.FROM_KEY));
        } else {
            this.fromDate.setMonth(this.fromDate.getMonth() - 1);
        }
        if (this.localStorageService.retrieve(this.TO_KEY) !== undefined) {
            this.toDate = new Date(this.localStorageService.retrieve(this.TO_KEY));
        }
        if (this.localStorageService.retrieve(this.FILTER_KEY) !== undefined) {
            this.query = this.localStorageService.retrieve(this.FILTER_KEY);
        }
        this.route.params
            .do(() => { this.loading = true; })
            .switchMap(params => {
                this.projectId = params['projectId'];
                this.refresh();
                return this.loadData(this.projectId);
            })
            .subscribe(() => {
                this.loading = false;
            });
    }

    public handleFromDateChange(dateFrom: Date) {
        this.fromDate = dateFrom;
        //  Change object in a way that angular detect the changes
        this.datePickerToOpts = $.extend({ startDate: dateFrom }, this.datePickerOpts);
        if (this.cachedResult === undefined) {
            this.refresh();
        } else {
            this.refreshAsync(this.cachedResult);
        }
        this.localStorageService.store(this.FROM_KEY, dateFrom);
    }

    public handleToDateChange(dateTo: Date) {
        this.toDate = dateTo;
        if (this.cachedResult === undefined) {
            this.refresh();
        } else {
            this.refreshAsync(this.cachedResult);
        }
        this.localStorageService.store(this.TO_KEY, dateTo);
    }

    public handleUpdateFilter(event: TicketFilter) {
        this.ticketFilter = event;
        this.refresh();
        this.localStorageService.store(this.FILTER_KEY, this.ticketFilter.toTicketFilterString());
    }


    private resetData() {
        this.actualData = [];
        this.idealData = [];
        this.lineChartLabels = [];
    }

    private updateChart() {
        this.lineChartData = [
            { data: this.actualData, label: this.lineChartData[0].label },
            { data: this.idealData, label: this.lineChartData[1].label }
        ];
    }

    private addDay(actual: number, ideal: number, label: string) {
        this.actualData.push(actual);
        this.idealData.push(ideal);
        this.lineChartLabels.push(label);
    }

    private utcRemoveTime(timestamp: Number): number {
        return moment(timestamp).startOf('day').valueOf();
    }

    private getStoryPointPerTicket(uuid: string): number {
        return this.tickets.get(uuid).storyPoints;
    }

    private loadData(projectId: string) {
        let ticketTagsObs = this.apiCallService
            .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, projectId, p))
            .map(tts => idListToMap(tts).map(tt => new TicketOverviewTag(tt)).toMap());
        let projectUsersObs = this.apiCallService
            .callNoError<UserResultJson[]>(p => this.projectApi.listProjectUsersUsingGETWithHttpInfo(projectId, p))
            .map(users => idListToMap(users).map(user => new TicketOverviewUser(user)).toMap());
        let o = Observable.zip(ticketTagsObs, projectUsersObs);
        o.subscribe(tuple => {
            this.allTicketTagsForFilter = tuple[0];
            this.allProjectUsers = tuple[1];
        });
        return o.map(it => undefined);

    }

    private refresh(): Observable<any> {
        let rawTicketStoryPointObs = this.apiCallService
            .callNoError<TicketStoryPointResultJson[]>(p => this.ticketApi
                .listTicketsStoryPointsUsingGETWithHttpInfo(this.projectId,
                this.ticketFilter.ticketNumbers, this.ticketFilter.title, this.ticketFilter.tags, this.ticketFilter.users,
                this.ticketFilter.progressOne, this.ticketFilter.progressTwo, this.ticketFilter.progressGreater,
                this.ticketFilter.dueDateOne, this.ticketFilter.dueDateTwo, this.ticketFilter.dueDateGreater,
                this.ticketFilter.storyPointsOne, this.ticketFilter.storyPointsTwo, this.ticketFilter.storyPointsGreater,
                this.ticketFilter.subTicket, p));

        rawTicketStoryPointObs.subscribe(
            result => {
                this.startData = 0;
                this.tickets.clear();
                if (result !== undefined) {
                    result.forEach(
                        next => {
                            if (next.open) {
                                this.startData += next.storyPoints;
                            }
                            this.tickets.set(next.id, next);
                        });
                }
            },
            error => { },
            // Subscription Completed
            () => {
                const rawTicketEventsObs = this.apiCallService.callNoError<TicketEventResultJson[]>(p =>
                    this.ticketEventApi.listTicketStateChangedEventsUsingPOSTWithHttpInfo(Array.from(this.tickets.keys()), p));

                rawTicketEventsObs.subscribe(
                    tuple => {
                        this.refreshAsync(tuple);
                    });

            });
        return rawTicketStoryPointObs.map(it => undefined);
    }
    private refreshAsync(result: TicketEventResultJson[]) {
        if (result === undefined) {
            return;
        }
        this.cachedResult = $.extend(true, [], result);
        const ticketEvents = new Map<Number, TicketEventResultJson[]>();
        const now = moment().valueOf();
        const fromMoment = moment(this.fromDate).startOf('day');
        let daysBetween: number;
        let idealData: number;
        let actualData: number;
        let idealDecreasePerDay: number;
        let startLines = this.startData.valueOf();
        result.forEach(element => {
            const dateUtc = this.utcRemoveTime(element.time);
            if (dateUtc > fromMoment.valueOf() && dateUtc < now) {
                const storyPoints = this.getStoryPointPerTicket(element.ticketId);
                if ((<TicketEventStateChanged>element).dstState) {
                    // Ticket was openend: Subtract it's Story Points, because we want to go back in time
                    startLines -= storyPoints;
                } else {
                    // Ticket was closed
                    startLines += storyPoints;
                }
            }
            let list = ticketEvents.get(dateUtc);
            if (list === undefined) {
                list = [];
            }
            list.push(element);
            ticketEvents.set(dateUtc, list);
        });
        idealData = actualData = startLines;

        // Max 10.000 days back - otherwise I am sure something is wrong with the dates
        daysBetween = Math.min(moment(this.toDate).diff(fromMoment, 'days') + 1, 10000);
        this.resetData();
        for (let i = 0; i < daysBetween; i++) {
            const ticketEventThatDay = ticketEvents.get(this.utcRemoveTime(fromMoment.valueOf()));
            if (ticketEventThatDay !== undefined) {
                ticketEventThatDay.forEach(ticketEvent => {
                    const storyPoints = this.getStoryPointPerTicket(ticketEvent.ticketId);
                    if ((<TicketEventStateChanged>ticketEvent).dstState) {
                        // Ticket was openend: Add it's Story Points, because we want to go forward in time
                        actualData += storyPoints;
                    } else {
                        // Ticket was closed
                        actualData -= storyPoints;
                    }
                });
            }
            if (fromMoment.valueOf() > now) {
                actualData = undefined;
            }
            if (i === 0) {
                idealData = actualData;
                idealDecreasePerDay = idealData / (daysBetween - 1);
            }
            this.addDay(actualData, Math.round(idealData * 10) / 10, fromMoment.format('YYYY-MM-DD'));
            idealData -= idealDecreasePerDay;
            fromMoment.add(1, 'day');
        }
        this.updateChart();
    }
}


interface TicketEventStateChanged {
    dstState?: boolean;
    srcState?: boolean;
}
