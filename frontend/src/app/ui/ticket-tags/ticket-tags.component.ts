import { Component, OnInit } from '@angular/core';
import { ApiCallService } from '../../service';
import { TickettagApi } from '../../api/api/TickettagApi';
import { TickettaggroupApi } from '../../api/api/TickettaggroupApi';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketTagResultJson } from '../../api/model/TicketTagResultJson';
import { TicketTagGroupResultJson } from '../../api/model/TicketTagGroupResultJson';

import { Observable } from 'rxjs';

@Component({
    selector: 'tt-ticket-tags',
    templateUrl: './ticket-tags.component.html',
    styleUrls: ['./ticket-tags.component.scss'],
})
export class TicketTagsComponent implements OnInit {
    loading = true;
    projectId: string;
    ticketTagGroupId: string;
    cu = false;
    mode = '';
    toUpdatetTcketTag: TicketTagResultJson = undefined;
    tagGroups: TicketTagGroupResultJson[] = [];
    private ticketTags: TicketTagResultJson[] = [];

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private ticketTagApi: TickettagApi,
        private ticketTagGroupApi: TickettaggroupApi,
        private apiCallService: ApiCallService) {
    }

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

    public loadTags() {
        console.log("hallo");
    }

    private refresh(): Observable<void> {
        let tagGroups = this.apiCallService
            .callNoError<TicketTagGroupResultJson[]>(h => this.ticketTagGroupApi
                .listTicketTagGroupsUsingGETWithHttpInfo(this.projectId, h));
        return Observable
            .zip(tagGroups)
            .do(tuple => {
                this.tagGroups = tuple[0];
            })
            .map(it => undefined)
            .catch(err => Observable.empty<void>());
    }

    onDeleteClicked(tag: TicketTagResultJson) {
        this.apiCallService
            .call<any>(h => this.ticketTagApi.deleteTicketTagUsingDELETEWithHttpInfo(tag.id, h))
            .subscribe(param => {
                this.refresh().subscribe();
            }
            );
    }

    onStartCreate() {
        this.mode = 'Create';
        this.cu = true;
    }

    onEditClicked(tag: TicketTagResultJson) {
        this.toUpdatetTcketTag = tag;
        this.cu = true;
        this.mode = 'Update';
    }

    onStopCreate() {
        this.cu = false;
    }

    finishCreateUpdate() {
        this.cu = false;
        this.refresh().subscribe();
    }
}
