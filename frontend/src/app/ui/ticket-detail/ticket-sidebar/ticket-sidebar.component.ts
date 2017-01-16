import { Component, Input, EventEmitter, Output, OnChanges, SimpleChanges, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { UserApi, UserResultJson } from '../../../api';
import { ApiCallService } from '../../../service';
import { using } from '../../../util/using';
import * as imm from 'immutable';
import { TicketDetail, TicketDetailAssTag, TicketDetailUser } from '../ticket-detail';

const Awesomplete = require('awesomplete/awesomplete');

// http://stackoverflow.com/questions/1787322/htmlspecialchars-equivalent-in-javascript/4835406#4835406
function escapeHtml(text: string): string {
  const map: {[key: string]: string} = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    '\'': '&#039;',
  };
  return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

type Assignment = {
  user: TicketDetailUser,
  tags: imm.List<{ id: string, transient: boolean }>
};

@Component({
  selector: 'tt-ticket-sidebar',
  templateUrl: './ticket-sidebar.component.html',
  styleUrls: ['./ticket-sidebar.component.scss']
})
export class TicketSidebarComponent implements OnChanges {
  @Input() ticket: TicketDetail;
  @Input() allAssignmentTags = imm.Map<string, TicketDetailAssTag>();
  private assignments: imm.List<Assignment>;

  @Output() readonly assignmentAdd = new EventEmitter<{user: string, tag: string}>();
  @Output() readonly assignmentRemove = new EventEmitter<{user: string, tag: string}>();
  @Output() readonly userAdd = new EventEmitter<TicketDetailUser>();

  private adding = false;
  private awesomeplete: any = null;
  private input: HTMLInputElement = null;
  private newUserName = '';

  constructor(
    private userApi: UserApi,
    private apiCallService: ApiCallService,
    private router: Router,
    private elementRef: ElementRef) {
  }

  ngOnChanges(changes: SimpleChanges) {
    if ('ticket' in changes || 'allAssignmentTags' in changes) {
      this.assignments = this.ticket.users
        .map((tags, user) => ({
          user: user,
          tags: tags.map(t => ({id: t.tag.id, transient: t.transient})).toList()
        }))
        .sort(using<Assignment>(it => it.user.name.toLocaleLowerCase()))
        .toList();
    }
  }

  onTagAdd(userId: string, tagId: string) {
    this.assignmentAdd.emit({ user: userId, tag: tagId });
  }

  onTagRemove(userId: string, tagId: string) {
    this.assignmentRemove.emit({ user: userId, tag: tagId });
  }

  onShowAdd() {
    this.adding = true;
    window.setTimeout(() => {
      this.input = this.elementRef.nativeElement.querySelector('.add-assigned-user input');
      this.awesomeplete = new Awesomplete(this.input, {
        list: [],
        minChars: 0,
        autoFirst: true,
        filter: (text: string, currentInput: string) => true
      });
      this.input = this.awesomeplete.input;
      this.input.value = this.newUserName;
      this.input.select();

      this.input.addEventListener('keydown', (ev: KeyboardEvent) => {
        if (ev.key === 'Enter') {
          this.onAdd();
          this.input.value = '';
        } else if (ev.key === 'Escape') {
          this.adding = this.input.disabled;
        }
      });
      this.input.addEventListener('blur', () => {
        this.adding = this.input.disabled;
      });
      this.input.addEventListener('input', () => {
        this.newUserName = this.input.value;
        this.refreshList();
      });
      this.input.addEventListener('awesomplete-select', (ev: any) => {
        this.newUserName = ev.text.value;
        this.refreshList();
      });

      this.refreshList();
    });
  }

  onAdd() {
    this.input.disabled = true;
    let username = this.newUserName.trim();
    if (username) {
      // TODO graceful error handling
      this.apiCallService
        .callNoError<UserResultJson>(p => this.userApi.getUserByUsernameUsingGETWithHttpInfo(username, p))
        .map(user => new TicketDetailUser(user))
        .subscribe(user => {
          this.input.disabled = false;
          this.input.focus();
          this.userAdd.emit(user);
          this.newUserName = '';
          this.input.value = '';
          this.refreshList();
        });
    }
  }

  assignedUserTrackBy(index: number, item: Assignment): string {
    return item.user.id;
  }

  private refreshList() {
    this.apiCallService
      .callNoError<UserResultJson[]>(p => this.userApi.listUsersFuzzyUsingGETWithHttpInfo(
        this.ticket.projectId,
        this.newUserName,
        ['USERNAME_ASC', 'NAME_ASC', 'MAIL_ASC'],
        p))
      .subscribe(users => {
        this.awesomeplete.list = users.map(user => ({
          value: user.username,
          label: escapeHtml(`${user.username} (${user.name} <${user.mail}>)`)
        }));
        this.awesomeplete.open();
      });
  };
}
