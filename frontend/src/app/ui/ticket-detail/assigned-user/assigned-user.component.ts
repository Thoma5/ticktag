import {Component, Input, EventEmitter, Output, OnInit} from '@angular/core';
import {TicketDetailAssTag, TicketDetailUser} from '../ticket-detail';
import * as imm from 'immutable';
import {MemberApi} from '../../../api/api/MemberApi';
import {MemberResultJson} from '../../../api/model/MemberResultJson';
import {ApiCallService} from '../../../service/api-call/api-call.service';

@Component({
  selector: 'tt-assigned-user',
  templateUrl: './assigned-user.component.html',
  styleUrls: ['./assigned-user.component.scss']
})
export class AssignedUserComponent implements OnInit {
  @Input() user: TicketDetailUser;
  @Input() tags: imm.List<{ id: string, transient: boolean }>;
  @Input() allTags: imm.Map<string, TicketDetailAssTag>;
  @Input() projectId: string;
  @Input() editable: boolean = true;

  @Output() readonly tagAdd = new EventEmitter<string>();
  @Output() readonly tagRemove = new EventEmitter<string>();

  constructor(private memberApi: MemberApi,
              private apiCallService: ApiCallService) {
  }

  ngOnInit(): void {
    this.apiCallService
      .call<MemberResultJson>(p => this.memberApi.getMemberUsingGETWithHttpInfo(this.user.id, this.projectId, p))
      .subscribe(result => {
        if (result.isValid) {
          console.log(result.result.defaultAssignmentTagId);
          if (this.tags.size === 0 && result.result.defaultAssignmentTagId != null) {
            this.tagAdd.emit(result.result.defaultAssignmentTagId);
          }
        }
      });

  }
}
