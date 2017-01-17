import { Component, OnInit, ViewContainerRef } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Overlay } from 'angular2-modal';
import { ApiCallService } from '../../service';
import {
  TicketApi, TicketOverviewResultJson, PageTicketOverviewResultJson, AssignmenttagApi,
  AssignmentTagResultJson, UserResultJson, TicketTagResultJson,
  TimeCategoryJson,
  TickettagApi, TicketuserrelationApi, TickettagrelationApi, ProjectApi,
  TimecategoryApi
} from '../../api';
import {
  TicketOverview, TicketOverviewTag, TicketOverviewAssTag, TicketOverviewUser,
  TicketOverviewTimeCategory
} from './ticket-overview';
import { TicketFilter } from './ticket-filter/ticket-filter';
import { TicketCreateEvent } from '../ticket-detail/ticket-create/ticket-create.component';
import { idListToMap } from '../../util/listmaputils';
import * as imm from 'immutable';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs/Subject';
import { showValidationError } from '../../util/error';

@Component({
  selector: 'tt-ticket-overview',
  templateUrl: './ticket-overview.component.html',
  styleUrls: ['./ticket-overview.component.scss']
})
export class TicketOverviewComponent implements OnInit {
  private loading = true;
  private reloading = true;
  private tickets: TicketOverview[] = [];
  private allAssignmentTags: imm.Map<string, TicketOverviewAssTag>;
  private allTicketTags: imm.Map<string, TicketOverviewTag>;
  private allTimeCategories: imm.Map<string, TicketOverviewTimeCategory>;
  private allProjectUsers: imm.Map<string, TicketOverviewUser>;
  private projectId: string | null = null;
  private filterTerms = new Subject<TicketFilter>();
  private ticketFilter: TicketFilter = new TicketFilter(undefined, undefined, undefined, undefined, undefined,
    undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined);
  sortprop = ['NUMBER_ASC'];
  offset = 0;
  limit = 30;
  totalElements = 0;
  query: string;
  rows: TicketOverview[] = [];
  iconsCss = {
    sortAscending: 'glyphicon glyphicon-chevron-down',
    sortDescending: 'glyphicon glyphicon-chevron-up',
    pagerLeftArrow: 'glyphicon glyphicon-chevron-left',
    pagerRightArrow: 'glyphicon glyphicon-chevron-right',
    pagerPrevious: 'glyphicon glyphicon-backward',
    pagerNext: 'glyphicon glyphicon-forward'
  };

  creating = false;
  createRunning = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private apiCallService: ApiCallService,
    private ticketApi: TicketApi,
    private assigmentTagsApi: AssignmenttagApi,
    private projectApi: ProjectApi,
    private ticketTagsApi: TickettagApi,
    private ticketAssignmentApi: TicketuserrelationApi,
    private ticketTagRelationApi: TickettagrelationApi,
    private timeCategoryApi: TimecategoryApi,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
    ) {
    overlay.defaultViewContainer = vcRef;
  }

  private newTicketOverview(
    currentTicketJson: TicketOverviewResultJson,
    interestingUsers: imm.Map<string, TicketOverviewUser>): TicketOverview {
    let to: TicketOverview = new TicketOverview(
      currentTicketJson,
      interestingUsers,
      this.allTicketTags,
      this.allAssignmentTags
    );
    return to;
  }


  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .switchMap(params => {
        let projectId = params['projectId'];
        this.projectId = projectId;
        this.route.queryParams.subscribe(p => {
          this.ticketFilter = new TicketFilter(p['title'] || undefined,
            p['ticketNumber'] ? (p['ticketNumber']).split(',')  : undefined,
            p['tag'] ? (p['tag']).split(',')  : undefined,
            p['user'] ? (p['user']).split(',') : undefined,
            p['progressOne'] || undefined, p['progressTwo'] || undefined, p['progressGreater'] || undefined,
            p['dueDateOne'] || undefined, p['dueDateTwo'] || undefined, p['dueDateGreater'] || undefined,
            p['spOne'] || undefined, p['spTwo'] || undefined, p['spGreater'] || undefined,
            p['open'] || undefined, p['parent'] || undefined);
          this.offset = p['page'] || 0;
          this.query = this.ticketFilter.toTicketFilterString();
        }, error => {});
        return this.refresh(this.ticketFilter);
      }, error => {})
      .subscribe(result => {
        this.loading = false;
      });
    this.filterTerms.debounceTime(900).switchMap(term => this.refresh(term)).subscribe(result => {}, error => {});
  }

  private refresh(ticketFilter?: TicketFilter): Observable<void> {
    this.reloading = true;
    if (ticketFilter === undefined) {
      ticketFilter = this.ticketFilter;
    } else {
      this.ticketFilter = ticketFilter;
    }
    this.location.replaceState('/project/' + this.projectId + '/tickets?page=' + this.offset
      + '&' + ticketFilter.toTicketFilterURLString());
    let rawTicketObs = this.apiCallService
      .callNoError<PageTicketOverviewResultJson>(p => this.ticketApi
        .listTicketsUsingGETWithHttpInfo(this.projectId, this.sortprop,
        ticketFilter.ticketNumbers, ticketFilter.title, ticketFilter.tags, ticketFilter.users,
        ticketFilter.progressOne, ticketFilter.progressTwo, ticketFilter.progressGreater,
        ticketFilter.dueDateOne, ticketFilter.dueDateTwo, ticketFilter.dueDateGreater,
        ticketFilter.storyPointsOne, ticketFilter.storyPointsTwo, ticketFilter.storyPointsGreater,
        ticketFilter.open, ticketFilter.parentNumber, this.offset, this.limit, p));
    let assignmentTagsObs = this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(p => this.assigmentTagsApi.listAssignmentTagsUsingGETWithHttpInfo(this.projectId, p))
      .map(ats => idListToMap(ats).map(at => new TicketOverviewAssTag(at, 0)).toMap());
    let ticketTagsObs = this.apiCallService
      .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, this.projectId, p))
      .map(tts => idListToMap(tts).map(tt => new TicketOverviewTag(tt)).toMap());
    let projectUsersObs = this.apiCallService
      .callNoError<UserResultJson[]>(p => this.projectApi.listProjectMembersUsingGETWithHttpInfo(this.projectId, undefined, p))
      .map(users => idListToMap(users).map(user => new TicketOverviewUser(user)).toMap());
    let timeCategoriesObs = this.apiCallService
      .callNoError<TimeCategoryJson[]>(p => this.timeCategoryApi.listProjectTimeCategoriesUsingGETWithHttpInfo(this.projectId, p))
      .map(tcs => idListToMap(tcs).map(tc => new TicketOverviewTimeCategory(tc)).toMap());
    return Observable
      .zip(rawTicketObs, assignmentTagsObs, ticketTagsObs, projectUsersObs, timeCategoriesObs)
      .do(
      tuple => {
        this.allAssignmentTags = tuple[1];
        this.allTicketTags = tuple[2];
        this.allProjectUsers = tuple[3];
        this.allTimeCategories = tuple[4];
        this.totalElements = tuple[0].totalElements;
        this.tickets = [];
        const start = this.offset * this.limit;
        const end = start + this.limit;
        let rows = [...this.rows];
        tuple[0].content.forEach(ticket =>
          this.tickets.push(this.newTicketOverview(ticket, tuple[3]))
        );
        for (let i = start; i < end; i++) {
          rows[i] = this.tickets[i - this.offset * this.limit];
        }
        this.rows = rows;
        this.reloading = false;
      })
      .map(it => undefined)
      .catch(err => Observable.empty<void>());
  }

  onPage(event: any) {
    this.limit = event.limit;
    this.offset = event.offset;
    this.refresh().subscribe(result => {}, error => {});
  }

  onSort(event: any) {
    if (event.sorts[0].prop === 'title') {
      this.sortprop = ['TITLE_' + event.sorts[0].dir.toUpperCase()];
    } else if (event.sorts[0].prop === 'storyPoints') {
      this.sortprop = ['STORY_POINTS_' + event.sorts[0].dir.toUpperCase()];
    } else if (event.sorts[0].prop === 'number') {
      this.sortprop = ['NUMBER_' + event.sorts[0].dir.toUpperCase()];
    } else if (event.sorts[0].prop === 'dueDate') {
      this.sortprop = ['DUE_DATE_' + event.sorts[0].dir.toUpperCase()];
    } else if (event.sorts[0].prop === 'progress') {
      this.sortprop = ['PROGRESS_' + event.sorts[0].dir.toUpperCase()];
    }
    this.refresh().subscribe(result => {}, error => {});
  }

  updateFilter(event: TicketFilter) {
    this.offset = 0;
    this.filterTerms.next(event);
  }

  onTagClicked(event: TicketOverviewTag) {
    this.query = '!tag:' + event.normalizedName;
  }

  onUserClicked(event: TicketOverviewUser) {
    this.query = '!user:' + event.username;
  }
  activate(event: any) {
    if (event.type === 'keydown' && event.event.code === 'Enter') {
      this.router.navigate(['/project', this.projectId, 'ticket', event.row.id]);
    }
  }

  onStartCreate() {
    this.creating = true;
  }

  onStopCreate() {
    this.creating = false;
  }

  onTicketCreate(val: TicketCreateEvent): void {
    let obs = this.apiCallService
      .call(p => this.ticketApi.createTicketUsingPOSTWithHttpInfo({
        title: val.title,
        open: true,
        storyPoints: null,
        initialEstimatedTime: null,
        currentEstimatedTime: null,
        dueDate: null,
        projectId: val.projectId,
        description: val.description,
        ticketAssignments: [],
        subTickets: [],
        existingSubTicketIds: [],
        parentTicketId: null,
        commands: val.commands.toArray(),
      }, p));

    this.createRunning = true;
    obs
      .do(() => this.createRunning = false)
      .subscribe(result => {
      if (!result.isValid) {
        showValidationError(this.modal, result);
      } else {
        this.creating = false;
        this.refresh().subscribe();
      }
    }, error => {});
  }
}
