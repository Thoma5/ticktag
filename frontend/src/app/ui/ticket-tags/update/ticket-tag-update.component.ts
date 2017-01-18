import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { ApiCallService, ApiCallResult } from '../../../service';
import { showValidationError } from '../../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { UpdateTicketTagRequestJson } from '../../../api/model/UpdateTicketTagRequestJson';
import { TicketTagResultJson } from '../../../api/model/TicketTagResultJson';
import { TicketTagGroupResultJson } from '../../../api/model/TicketTagGroupResultJson';
import { TickettagApi } from '../../../api/api/TickettagApi';


@Component({
  selector: 'tt-ticket-tag-update',
  templateUrl: './ticket-tag-update.component.html',
  styleUrls: ['./ticket-tag-update.component.scss']
})

export class TicketTagUpdateComponent implements OnInit {
  request: UpdateTicketTagRequestJson = {
    name: undefined,
    color: undefined,
    order: 0,
    ticketTagGroupId: ''
  };
  active: Boolean;
  working = false;
  @Input() ticketTag: TicketTagResultJson;
  @Input() tagGroups: TicketTagGroupResultJson[];
  @Output() readonly updated = new EventEmitter<TicketTagResultJson>();

  constructor(
    private apiCallService: ApiCallService,
    private ticketTagApi: TickettagApi,
    private modal: Modal) { }

  ngOnInit(): void {
    this.revert();
  }

  onSubmit(): void {
    this.working = true;
    this.request.color = this.request.color.substring(1);
    this.apiCallService
      .call<TicketTagResultJson>(h => this.ticketTagApi
        .updateTicketTagUsingPUTWithHttpInfo(this.ticketTag.id, this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
          this.request.color = '';
          this.request.ticketTagGroupId = '';
          this.updated.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  revert() {
    this.request.name = this.ticketTag.name;
    this.request.color = '#' + this.ticketTag.color;
    this.request.ticketTagGroupId = this.ticketTag.ticketTagGroupId;
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
