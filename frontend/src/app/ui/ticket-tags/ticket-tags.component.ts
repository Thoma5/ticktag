import { Component, OnInit } from '@angular/core';
import { ApiCallService } from '../../service';
import { TickettagApi } from '../../api/api/TickettagApi';
import { TickettaggroupApi } from '../../api/api/TickettaggroupApi';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketTagResultJson } from '../../api/model/TicketTagResultJson';
import { TicketTagGroupResultJson } from '../../api/model/TicketTagGroupResultJson';
import * as imm from 'immutable';
import { Observable } from 'rxjs';
import { idListToMap } from '../../util/listmaputils';

@Component({
    selector: 'tt-ticket-tags',
    templateUrl: './ticket-tags.component.html',
    styleUrls: ['./ticket-tags.component.scss'],
})
export class TicketTagsComponent implements OnInit {
    loading = true;
    projectId: string;
    ticketTagGroupId = 'notSelected';
    currentTagGroup: TicketTagGroupResultJson;
    cu = false;
    mode = '';
    toUpdateTicketTag: TicketTagResultJson = undefined;
    private tagGroupsMap: imm.Map<string, TicketTagGroupResultJson>;
    private tagGroups: TicketTagGroupResultJson[] = [];
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

    public setTagGroup(): void {
        console.log("hallo" + this.ticketTagGroupId)
        if (this.ticketTagGroupId !== 'notSelected') {
            let tagGroup = this.apiCallService
                .callNoError<TicketTagGroupResultJson>(h => this.ticketTagGroupApi
                    .getTicketTagGroupUsingGETWithHttpInfo(this.ticketTagGroupId, h));
            Observable
                .zip(tagGroup)
                .do(tuple => {
                    this.currentTagGroup = tuple[0];
                })
                .catch(err => Observable.empty<void>());
        }
    }

    public getTagGroupById(id: string) {
        return this.tagGroups.find(g => g.id == id);
    }

    private refresh(): Observable<void> {
        let tagGroups = this.apiCallService
            .callNoError<TicketTagGroupResultJson[]>(h => this.ticketTagGroupApi
                .listTicketTagGroupsUsingGETWithHttpInfo(this.projectId, h));


        let tags = this.apiCallService
            .callNoError<TicketTagResultJson[]>(h => this.ticketTagApi
                .listTicketTagsUsingGETWithHttpInfo(undefined, this.projectId, h));

        return Observable
            .zip(tags, tagGroups)
            .do(tuple => {
                this.ticketTags = tuple[0];
                this.tagGroups = tuple[1];
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
        this.toUpdateTicketTag = tag;
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
