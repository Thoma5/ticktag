import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService } from '../../service';
import { TicketApi, TicketResultJson , AssignmenttagApi,  TickettagApi } from '../../api';
import {  } from '../../api';

@Component({
  selector: 'tt-ticket-overview',
  templateUrl: './ticket-overview.component.html',
})
export class TicketOverviewComponent implements OnInit {
  private loading = true;
  tickets: TicketResultJson[] | null = null;
  columns = [
    { prop: 'number' },
    { prop: 'title' },
    { prop: 'ticketAssignments' },
     { prop: 'storyPoints' },
     { prop: 'dueDate' }
  ];

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private ticketApi: TicketApi,
    private assigmentTagsApi: AssignmenttagApi,
    private tickettagApi: TickettagApi,
    private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService) {
  }

  ngOnInit(): void {
    this.tickets = [];
    let projectId = '00000000-0002-0000-0000-000000000001';
    this.apiCallService
         .callNoError<TicketResultJson[]>(h => this.ticketApi.listTicketsUsingGETWithHttpInfo(projectId , h))
         .subscribe(tickets => {
           this.tickets = tickets;
           this.loading = false; });
      });
  }
  }

}
