import { Component, Input, Output, EventEmitter, AfterViewInit, OnChanges, OnDestroy, SimpleChanges } from '@angular/core';
import {
  TicketDetailAssTag, TicketDetail, TicketDetailTimeCategory, TicketDetailTag
} from '../ticket-detail';
import * as imm from 'immutable';
import { CommandTextviewSaveEvent } from '../../../util/command-textview/command-textview.component';
import * as grammar from '../../../service/command/grammar';
import { Subject, ReplaySubject, Observable, Subscription } from 'rxjs';

export type SubticketCreateEvent = {
  projectId: string,
  parentTicketId: string,
  title: string,
  description: string,
  commands: imm.List<grammar.Cmd>,
};

export type ResetEvent = {
  title: string,
  description: string,
};

@Component({
  selector: 'tt-subticket-add',
  templateUrl: './subticket-add.component.html',
  styleUrls: ['./subticket-add.component.scss']
})
export class SubticketAddComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input() parentTicket: TicketDetail;
  @Input() allTicketTags: imm.Map<string, TicketDetailTag>;
  @Input() allTimeCategories: imm.Map<string, TicketDetailTimeCategory>;
  @Input() allAssignmentTags: imm.Map<string, TicketDetailAssTag>;
  readonly activeTags = imm.List.of();
  readonly assignedUsers = imm.List.of();

  @Input() readonly resetEventObservable: Observable<ResetEvent>;
  @Output() readonly ticketAdd = new EventEmitter<SubticketCreateEvent>();
  private readonly resetEventSubject = new Subject<ResetEvent>();
  private resetEventSubscription: Subscription;
  private readonly textViewResetEventSubject = new Subject<string>();
  // Replays the last n (=1) events when subscribed, needed because the directive subscribes after our AfterViewInit event
  private readonly titleFocusEventSubject = new ReplaySubject<void>(1);

  editing: boolean = false;
  title: string = '';
  description: CommandTextviewSaveEvent = this.getEmptyDescription();

  ngAfterViewInit(): void {
    this.titleFocusEventSubject.next(undefined);
    this.resubscribeResetEvent();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ('resetEventObservable' in changes) {
      this.resubscribeResetEvent();
    }
  }

  ngOnDestroy(): void {
    this.unsubscribeResetEvent();
  }

  startEditing() {
    this.editing = true;
    this.description = this.getEmptyDescription();
    this.resetEventSubject.next({
      title: '',
      description: this.description.text,
    });
    this.titleFocusEventSubject.next(undefined);
  }

  finishEditing(restart: boolean) {
    this.editing = false;
    if (restart) {
      this.startEditing();
    }
  }

  onSubmit(): void {
    let event = {
      projectId: this.parentTicket.projectId,
      parentTicketId: this.parentTicket.id,
      title: this.title,
      description: this.description.text,
      commands: this.description.commands,
    };
    this.ticketAdd.emit(event);
    this.finishEditing(true);
  }

  onAbort(): void {
    this.finishEditing(false);
  }

  private resubscribeResetEvent() {
    this.unsubscribeResetEvent();
    let obs: Observable<ResetEvent> = this.resetEventSubject;
    if (this.resetEventObservable != null) {
      obs = Observable.merge(obs, this.resetEventObservable);
    }

    this.resetEventSubscription = obs.subscribe(event => {
      this.title = event.title;
      this.textViewResetEventSubject.next(event.description);
      this.titleFocusEventSubject.next(undefined);
    });
  }

  private unsubscribeResetEvent() {
    if (this.resetEventSubscription != null) {
      this.resetEventSubscription.unsubscribe();
    }
  }

  private getEmptyDescription(): CommandTextviewSaveEvent {
    return { commands: imm.List.of<grammar.Cmd>(), text: '' };
  }
}
