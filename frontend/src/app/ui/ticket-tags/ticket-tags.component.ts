import { Component, OnInit } from '@angular/core';
import { ApiCallService } from '../../service';
import { TickettagApi } from '../../api/api/TickettagApi';
import { TickettaggroupApi } from '../../api/api/TickettaggroupApi';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketTagResultJson } from '../../api/model/TicketTagResultJson';
import { CreateTicketTagGroupRequestJson } from '../../api/model/CreateTicketTagGroupRequestJson';
import { UpdateTicketTagGroupRequestJson } from '../../api/model/UpdateTicketTagGroupRequestJson';
import { TicketTagGroupResultJson } from '../../api/model/TicketTagGroupResultJson';
import { Observable } from 'rxjs';
import { Modal } from 'angular2-modal/plugins/bootstrap';

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
    private tagGroups: TicketTagGroupResultJson[] = [];
    private ticketTags: TicketTagResultJson[] = [];

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private ticketTagApi: TickettagApi,
        private ticketTagGroupApi: TickettaggroupApi,
        private apiCallService: ApiCallService,
        private modal: Modal) {
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
                this.ticketTags = tuple[0].filter(t => t.disabled === false);
                this.tagGroups = tuple[1];
                this.ticketTagGroupId = 'notSelected';
            })
            .map(it => undefined)
            .catch(err => Observable.empty<void>());
    }

    setTagGroup(): void {
        let g = this.getTagGroupById(this.ticketTagGroupId);
        this.currentTagGroup = { id: g.id, name: g.name, projectId: g.projectId, exclusive: g.exclusive, required: g.required };
    }

    getTagGroupById(id: string) {
        return this.tagGroups.find(g => g.id === id);
    }

    onDeleteClicked(tag: TicketTagResultJson) {
        this.modal.confirm()
      .size('sm')
      .isBlocking(true)
      .showClose(false)
      .body('Are you sure you that you want to delete this item?')
      .okBtn('Delete')
      .open()
      .then(a => {
        a.result.then(result => {
          // Delete clicked
      
        this.apiCallService
            .call<any>(h => this.ticketTagApi.deleteTicketTagUsingDELETEWithHttpInfo(tag.id, h))
            .subscribe(param => {
                this.refresh().subscribe();
            }
            );
              }).catch(result => {
          // Cancel clicked
        });
      });
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

    onNewTagGroupClicked() {
        this.ticketTagGroupId = 'new';
        this.currentTagGroup = { id: '', name: '', projectId: this.projectId, exclusive: false, required: false };
    }

    onSaveTagGroupClicked() {
        if (this.ticketTagGroupId === 'new') {
            let createTagGroup: CreateTicketTagGroupRequestJson = {
                name: this.currentTagGroup.name,
                projectId: this.projectId, exclusive: this.currentTagGroup.exclusive
            };
            this.apiCallService
                .call<TicketTagGroupResultJson>(h => this.ticketTagGroupApi
                    .createTicketTagGroupUsingPOSTWithHttpInfo(createTagGroup, h))
                .subscribe(
                result => {
                    this.finishCreateUpdate();
                },
                undefined,
                () => { this.finishCreateUpdate(); });
        } else {
            let updateTagGroup: UpdateTicketTagGroupRequestJson = {
                name: this.currentTagGroup.name,
                exclusive: this.currentTagGroup.exclusive
            };
            this.apiCallService
                .call<TicketTagGroupResultJson>(h => this.ticketTagGroupApi
                    .updateTicketTagGroupUsingPUTWithHttpInfo(this.currentTagGroup.id, updateTagGroup, h))
                .subscribe(
                result => {
                    this.finishCreateUpdate();
                },
                undefined,
                () => { this.finishCreateUpdate(); });
        }
    }


    onStopCreate() {
        this.cu = false;
    }

    finishCreateUpdate() {
        this.cu = false;
        this.refresh().subscribe();
    }
}
