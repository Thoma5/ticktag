import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService } from '../../service';
import {
  TicketApi, TicketResultJson, PageTicketResultJson, AssignmenttagApi,
  AssignmentTagResultJson, TicketTagResultJson,
  TickettagApi, GetApi, GetResultJson,
  TicketuserrelationApi, TickettagrelationApi
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
  loading = true;
  private tickets: TicketOverview[] = [];
  private allAssignmentTags: imm.Map<string, TicketOverviewAssTag>;
  private allTicketTags: imm.Map<string, TicketOverviewTag>;
  reload = true;
  asc = true;
  sortprop = ['NUMBER_ASC'];
  offset = 0;
  limit = 15;
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

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService,
    private ticketApi: TicketApi,
    private assigmentTagsApi: AssignmenttagApi,
    private ticketTagsApi: TickettagApi,
    private getApi: GetApi,
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
    console.log(to);
    return to;
  }


  ngOnInit(): void {
    this.route.params
      .switchMap(params => {
        let projectId = '' + params['projectId'];
        return this.refresh(projectId);
      });
  }

  private refresh(projectId: string): Observable<void> {
    console.log('loading data');
    let rawTicketObs = this.apiCallService
      .callNoError<PageTicketResultJson>(p => this.ticketApi
        .listTicketsUsingGETWithHttpInfo(projectId, this.sortprop, this.offset, this.limit, p));
    let assignmentTagsObs = this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(p => this.assigmentTagsApi.listAssignmentTagsUsingGETWithHttpInfo(projectId, p))
      .map(ats => idListToMap(ats).map(at => new TicketOverviewAssTag(at, 0)).toMap());  // TODO ordering
    let ticketTagsObs = this.apiCallService
      .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, projectId, p))
      .map(tts => idListToMap(tts).map(tt => new TicketOverviewTag(tt)).toMap());
    return Observable
      .zip(rawTicketObs, assignmentTagsObs, ticketTagsObs)
      .flatMap(tuple => {
        let ticketsResult = tuple[0];
        // We need all assigned users
        let wantedUserIds: string[] = [];
        ticketsResult.content.forEach(t => t.ticketUserRelations.map(ta => wantedUserIds.concat(ta.userId)));

        let getObs = this.apiCallService
          .callNoError<GetResultJson>(p => this.getApi.getUsingPOSTWithHttpInfo({ userIds: wantedUserIds }, p));

        return Observable.zip(Observable.of(tuple), getObs);
      })
      .do(
      tuple => {
        this.allAssignmentTags = tuple[0][1];
        this.allTicketTags = tuple[0][2];
        this.totalElements = tuple[0][0].totalElements;
        this.tickets = [];
        const start = this.offset * this.limit;
        const end = start + this.limit;
        let rows = [...this.rows];
        tuple[0][0].content.forEach(ticket =>
          this.tickets.concat(
            this.newTicketOverview(ticket, imm.Map(tuple[1].users).map(u => new TicketOverviewUser(u)).toMap()))
        );
        for (let i = start; i < end; i++) {
          rows[i] = this.tickets[i - this.offset * this.limit];
        }
        this.rows = rows;
        console.log(this.rows);
        this.loading = false;
        this.reload = false;
      })
      .map(it => undefined);
  }

  onPage(event) {
    this.reload = true;
    console.log('Page Event', event);
    this.limit = event.limit;
    this.offset = event.offset;
    this.reloadData();

  }

  onSort(event) {
    this.reload = true;
    console.log('Sort Event', event);
    this.asc = event.sorts[0].dir === 'asc' ? true : false;
    this.reloadData();
  }

  updateFilter(event) {
    this.reload = true;
    // TODO  filter our data
    this.offset = 0;
    this.reloadData();
  }

  reloadData() {
    this.route.params
      .do(() => { this.reload = true; })
      .switchMap(params => {
        let projectId = '' + params['projectId'];
        return this.refresh(projectId);
      });
  }


}
