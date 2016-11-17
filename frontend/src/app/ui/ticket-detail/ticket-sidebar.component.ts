import { Component, OnInit, Input } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
// import { Response, ResponseContentType } from '@angular/http';
import { ApiCallService } from '../../service';
import { TicketResultJson } from './ticket-detail.component';
import { Observable } from 'rxjs';
import {Duration, Instant} from 'js-joda';

@Component({
  selector: 'tt-ticket-sidebar',
  templateUrl: './ticket-sidebar.component.html',
  styleUrls: ['./ticket-sidebar.component.scss']
})
export class TicketSidebarComponent {
    @Input() ticket: TicketResultJson;


}
