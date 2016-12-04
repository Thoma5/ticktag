import { Component, Input } from '@angular/core';

@Component({
  selector: 'tt-subticket',
  templateUrl: './subticket.component.html',
  styleUrls: ['./subticket.component.scss']
})
export class SubticketComponent {
  @Input() projectId: number;
  @Input() ticketId: number;
  @Input() number: number;
  @Input() title: string;
  @Input() loggedTime: number;
  @Input() estimatedTime: number|null;
  @Input() initialEstimatedTime: number|null;
  @Input() open: boolean;
  @Input() loading: boolean = false;
  @Input() error: boolean = false;
  @Input() transient: boolean = false;


  @Input() showNumber: boolean = true;
  @Input() showProgress: boolean = true;
}
