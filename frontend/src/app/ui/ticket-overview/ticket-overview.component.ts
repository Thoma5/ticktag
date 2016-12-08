import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService } from '../../service';
import {
  TicketApi, TicketResultJson, PageTicketResultJson, AssignmenttagApi,
  AssignmentTagResultJson, UserResultJson, TicketTagResultJson,
  TickettagApi, TicketuserrelationApi, TickettagrelationApi, ProjectApi
} from '../../api';
import {
  TicketOverview, TicketOverviewTag, TicketOverviewAssTag, TicketOverviewUser
} from './ticket-overview';
import { idListToMap } from '../../util/listmaputils';
import * as imm from 'immutable';
import { Observable } from 'rxjs';

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
  private allProjectUsers: imm.Map<string, TicketOverviewUser>;
  private projectId: string | null = null;
  sortprop = ['NUMBER_ASC'];
  offset = 0;
  limit = 10;
  totalElements = 0;
  rows: TicketOverview[] = [];
  iconsCss = {
    sortAscending: 'glyphicon glyphicon-chevron-down',
    sortDescending: 'glyphicon glyphicon-chevron-up',
    pagerLeftArrow: 'glyphicon glyphicon-chevron-left',
    pagerRightArrow: 'glyphicon glyphicon-chevron-right',
    pagerPrevious: 'glyphicon glyphicon-backward',
    pagerNext: 'glyphicon glyphicon-forward'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService,
    private ticketApi: TicketApi,
    private assigmentTagsApi: AssignmenttagApi,
    private projectApi: ProjectApi,
    private ticketTagsApi: TickettagApi,
    private ticketAssignmentApi: TicketuserrelationApi,
    private ticketTagRelationApi: TickettagrelationApi) {
  }

  private newTicketOverview(
    currentTicketJson: TicketResultJson,
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
        return this.refresh(projectId);
      })
      .subscribe(() => {
        this.loading = false;
      });
  }

  private refresh(projectId: string): Observable<void> {
    this.reloading = true;
    let rawTicketObs = this.apiCallService
      .callNoError<PageTicketResultJson>(p => this.ticketApi
        .listTicketsUsingGETWithHttpInfo(projectId, this.sortprop,
        undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, undefined, undefined, this.offset, this.limit, p));
    let assignmentTagsObs = this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(p => this.assigmentTagsApi.listAssignmentTagsUsingGETWithHttpInfo(projectId, p))
      .map(ats => idListToMap(ats).map(at => new TicketOverviewAssTag(at, 0)).toMap());  // TODO ordering
    let ticketTagsObs = this.apiCallService
      .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, projectId, p))
      .map(tts => idListToMap(tts).map(tt => new TicketOverviewTag(tt)).toMap());
    let projectUsersObs = this.apiCallService
      .callNoError<UserResultJson[]>(p => this.projectApi.listProjectUsersUsingGETWithHttpInfo(projectId, p))
      .map(users => idListToMap(users).map(user => new TicketOverviewUser(user)).toMap());
    return Observable
      .zip(rawTicketObs, assignmentTagsObs, ticketTagsObs, projectUsersObs)
      .do(
      tuple => {
        this.allAssignmentTags = tuple[1];
        this.allTicketTags = tuple[2];
        this.allProjectUsers = tuple[3];
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
      .map(it => undefined);
  }

  onPage(event: any) {
    this.limit = event.limit;
    this.offset = event.offset;
    this.refresh(this.projectId).subscribe();
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
    this.refresh(this.projectId).subscribe();
  }

  updateFilter(event: any) {
    // TODO  filter our data
    this.offset = 0;
    this.refresh(this.projectId).subscribe();
  }

  onTagClicked(event: TicketOverviewTag) {
    console.log(event); // TODO add to filter
  }
  activate(event: any) {
    if (event.type === 'keydown' && event.event.code === 'Enter') {
      this.router.navigate(['/project', this.projectId, 'ticket', event.row.id]);
    }
  }
}
