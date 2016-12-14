import { Component, OnInit } from '@angular/core';
import { ApiCallService } from '../../service/api-call/api-call.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { TicketEventResultJson } from '../../api/model/TicketEventResultJson';
import { TicketeventApi } from '../../api/api/TicketeventApi';
import * as moment from 'moment';
import { LocalStorageService } from 'ng2-webstorage';

@Component({
    selector: 'tt-burn-down-chart',
    templateUrl: './burn-down-chart.component.html',
    styleUrls: ['./burn-down-chart.component.scss']
})
export class BurnDownChartComponent implements OnInit {

    constructor(private route: ActivatedRoute,
        private router: Router,
        private apiCallService: ApiCallService,
        private TicketEventApi: TicketeventApi,
        private localStorageService: LocalStorageService
    ) {
        // Clone Options
        this.datePickerToOpts = $.extend({}, this.datePickerOpts);
    }

    private projectId: String;
    public search = 'TODO';
    private FROM_KEY = 'BURNDOWN_FROM';
    private TO_KEY = 'BURNDOWN_TO';
    public toDate = new Date();
    public fromDate = new Date();
    private idealData: number[] = [];
    private actualData: number[] = [];
    public datePickerOpts = {
        autoclose: true,
        todayBtn: 'linked',
        todayHighlight: true,
        assumeNearbyYear: true,
        format: 'yyyy-mm-dd'
    };

    public datePickerToOpts: any;

    handleFromDateChange(dateFrom: Date) {
        this.fromDate = dateFrom;
        //  Change object in a way that angular detect the changes
        this.datePickerToOpts = $.extend({ startDate: dateFrom }, this.datePickerOpts);
        this.refresh();
        this.localStorageService.store(this.FROM_KEY, dateFrom);
    }

    handleToDateChange(dateTo: Date) {
        this.toDate = dateTo;
        this.refresh();
        this.localStorageService.store(this.TO_KEY, dateTo);
    }

    resetData() {
        this.actualData = [];
        this.idealData = [];
        this.lineChartLabels = [];
    }

    updateChart() {
        this.lineChartData = [
            { data: this.actualData, label: this.lineChartData[0].label },
            { data: this.idealData, label: this.lineChartData[1].label }
        ];
    }

    addDay(actual: number, ideal: number, label: string) {
        this.actualData.push(actual);
        this.idealData.push(ideal);
        this.lineChartLabels.push(label);
    }

    //  lineChart
    public lineChartData: Array<any> = [
        { data: [], label: 'Actual Task Remaining' },
        { data: [], label: 'Ideal Tasks Remaining' },
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
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(77,83,96,1)'
        },
        { //  green
            backgroundColor: 'rgba(0,0,0,0)',
            borderColor: 'rgba(159, 221, 150, 1)',
            pointBackgroundColor: 'rgba(159, 221, 150, 1)',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(148,159,177,0.8)'
        },
    ];
    public lineChartLegend: boolean = true;
    public lineChartType: string = 'line';

    private loading = true;
    ngOnInit(): void {
        if (this.localStorageService.retrieve(this.FROM_KEY) !== undefined) {
            this.fromDate = new Date(this.localStorageService.retrieve(this.FROM_KEY));
        } else {
            this.fromDate.setMonth(this.fromDate.getMonth() - 1);
        }
        if (this.localStorageService.retrieve(this.TO_KEY) !== undefined) {
            this.toDate = new Date(this.localStorageService.retrieve(this.TO_KEY));
        }
        this.route.params
            .do(() => { this.loading = true; })
            .switchMap(params => {
                this.projectId = params['projectId'];
                return this.refresh();
            })
            .subscribe(() => {
                this.loading = false;
            });
    }

    private utcRemoveTime(timestamp: Number): number {
        return moment(timestamp).startOf('day').valueOf();
    }

    private getStoryPointPerTicket(uuid: string): number {
        return 10;
    }

    private refresh(): Observable<any> {
        const ticketids = ['00000000-0003-0000-0000-000000000003', '00000000-0003-0000-0000-000000000006'];
        const rawTicketEventsObs = this.apiCallService.callNoError<TicketEventResultJson[]>(p =>
            this.TicketEventApi.listTicketStateChangedEventsUsingGETWithHttpInfo(ticketids, p));
        const o = Observable.zip(rawTicketEventsObs);
        o.subscribe(
            tuple => {
                this.refeshAsync(tuple[0]);
            });
        return o.map(it => undefined);
    }

    private refeshAsync(result: TicketEventResultJson[]) {
        const ticketEvents = new Map<Number, TicketEventResultJson[]>();
        const now = moment().valueOf();
        const fromMoment = moment(this.fromDate).startOf('day');
        let daysBetween: number;
        let idealData: number;
        let actualData: number;
        let idealDecreasePerDay: number;
        let startLines = 100;

        result.forEach(element => {
            const dateUtc = this.utcRemoveTime(element.time);
            if (dateUtc > fromMoment.valueOf() && dateUtc < now) {
                const storyPoints = this.getStoryPointPerTicket(element.ticketId);
                if ((<TicketEventStateChanged>element).dstState) {
                    // Ticket was openend: Subtract it's Story Points, because we want to go back in time
                    startLines -= storyPoints;
                    console.log("Minus wegen" + element.ticketId);
                } else {
                    // Ticket was closed
                    startLines += storyPoints;
                    console.log("Plus wegen" + element.ticketId);
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
        idealDecreasePerDay = startLines / (daysBetween - 1);
        this.resetData();
        for (let i = 0; i < daysBetween; i++) {
            const ticketEventThatDay = ticketEvents.get(this.utcRemoveTime(fromMoment.valueOf()));
            if (ticketEventThatDay !== undefined) {
                ticketEventThatDay.forEach(ticketEvent => {
                    const storyPoints = this.getStoryPointPerTicket(ticketEvent.ticketId);
                    if ((<TicketEventStateChanged>ticketEvent).dstState) {
                        // Ticket was openend: Add it's Story Points, because we want to go forward in time
                        actualData += storyPoints;
                        console.log("!!Plus wegen" + ticketEvent.ticketId);
                    } else {
                        // Ticket was closed
                        actualData -= storyPoints;
                        console.log("!!Minus wegen" + ticketEvent.ticketId);
                    }
                });
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
