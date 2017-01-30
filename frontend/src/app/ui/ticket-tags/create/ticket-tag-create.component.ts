import { Component, Output, EventEmitter, Input } from '@angular/core';
import { ApiCallService, ApiCallResult } from '../../../service';
import { showValidationError } from '../../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { CreateTicketTagRequestJson } from '../../../api/model/CreateTicketTagRequestJson';
import { TicketTagResultJson } from '../../../api/model/TicketTagResultJson';
import { TicketTagGroupResultJson } from '../../../api/model/TicketTagGroupResultJson';
import { TickettagApi } from '../../../api/api/TickettagApi';

@Component({
  selector: 'tt-ticket-tag-create',
  templateUrl: './ticket-tag-create.component.html',
  styleUrls: ['./ticket-tag-create.component.scss']
})

export class TicketTagCreateComponent {

  request: CreateTicketTagRequestJson = {
    name: '',
    color: '',
    order: 0,
    ticketTagGroupId: '',
    autoClose: false
  };
  working = false;
  @Input() tagGroups: TicketTagGroupResultJson[];
  @Output() readonly created = new EventEmitter<TicketTagResultJson>();

  constructor(
    private apiCallService: ApiCallService,
    private assignmentTagApi: TickettagApi,
    private modal: Modal) { }

  onSubmit(): void {
    this.working = true;
    this.request.ticketTagGroupId = this.request.ticketTagGroupId;
    this.request.color = this.request.color.substring(1);
    this.apiCallService
      .call<TicketTagResultJson>(h => this.assignmentTagApi.createTicketTagUsingPOSTWithHttpInfo(this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
          this.request.color = '';
          this.request.order = 0;
          this.request.autoClose = false;
          this.created.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
