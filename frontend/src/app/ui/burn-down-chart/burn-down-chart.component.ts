import { Component, OnInit } from '@angular/core';
import * as imm from 'immutable';
import { ApiCallService } from '../../service/api-call/api-call.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { GetResultJson } from '../../api/model/GetResultJson';
import { GetApi } from '../../api/api/GetApi';
import * as moment from 'moment';

@Component({
    selector: 'tt-burn-down-chart',
    templateUrl: './burn-down-chart.component.html',
    styleUrls: ['./burn-down-chart.component.scss']
})
export class BurnDownChartComponent implements OnInit {

    constructor(private route: ActivatedRoute,
        private router: Router,
        private apiCallService: ApiCallService,
        private getApi: GetApi,

    ) {
        //Clone Options
        this.datePickerToOpts = $.extend({}, this.datePickerOpts);
        this.addDay(1, 1, "test");
    }
    private projectId: String;

    public search = 'TODO';
    public fromDate = new Date();
    public toDate = new Date();
    private daysBetween: number;
    private startIdealLine: number;
    private idealDecreasePerDay: number;
    private idealData: number[] = [];
    private actualData: number[] = [];

    public datePickerOpts = {
        autoclose: true,
        todayBtn: 'linked',
        todayHighlight: true,
        assumeNearbyYear: true,
        format: 'yyyy-mm-dd'
    }

    public datePickerToOpts: any;

    handleFromDateChange(dateFrom: Date) {
        this.fromDate = dateFrom;
        // Change object in a way that angular detect the changes
        this.datePickerToOpts = $.extend({ startDate: dateFrom }, this.datePickerOpts);

        //Reload Data
        this.refresh();
    }

    handleToDateChange(dateTo: Date) {
        this.toDate = dateTo;
        this.refresh();
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

    // lineChart
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
        }
    };
    public lineChartColors: Array<any> = [
        { // red
            backgroundColor: 'rgba(0,0,0,0)',
            borderColor: 'rgba(229, 107, 107, 1)',
            pointBackgroundColor: 'rgba(229, 107, 107, 1)',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(77,83,96,1)'
        },
        { // green
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

    private refresh(): Observable<void> {
        console.log('projectId is' + this.projectId);
        const fromMoment = moment(this.fromDate);
        this.startIdealLine = 100;
        this.daysBetween = moment(this.toDate).diff(fromMoment, 'days') + 1;
        this.idealDecreasePerDay = this.startIdealLine / (this.daysBetween - 1);

        console.log(this.daysBetween);

        this.resetData();
        for (var i = 0; i < this.daysBetween; i++) {
            this.addDay(100, Math.round(this.startIdealLine * 10) / 10, fromMoment.format('YYYY-MM-DD'));
            this.startIdealLine = this.startIdealLine - this.idealDecreasePerDay;
            fromMoment.add(1, 'day');
        }
        this.updateChart();

        const fakeData = [50, 30, 20, 10];
        //return Observable.zip().flatMap().do().map();
        return Observable.of(fakeData).map(it => undefined);
    }
}
