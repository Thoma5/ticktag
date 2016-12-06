import { Component, OnInit } from '@angular/core';
import * as imm from 'immutable';
import { ApiCallService } from '../../service/api-call/api-call.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { GetResultJson } from '../../api/model/GetResultJson';
import { GetApi } from '../../api/api/GetApi';

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

    }

    private loading = true;
    ngOnInit(): void {
        this.route.params
            .do(() => { this.loading = true; })
            .switchMap(params => {
                let projectId = params['projectId'];
                return this.refresh(projectId);
            })
            .subscribe(() => {
                this.loading = false;
            });
    }

    private refresh(projectId: string): Observable<void> {
        console.log('projectId is'+projectId);
        const fakeData = [50, 30, 20, 10];
        //return Observable.zip().flatMap().do().map();
        return Observable.of(fakeData).map(it => undefined);

    }

}