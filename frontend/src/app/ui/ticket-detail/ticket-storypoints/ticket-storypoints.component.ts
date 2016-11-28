import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'tt-ticket-storypoints',
    templateUrl: './ticket-storypoints.component.html',
    styleUrls: ['./ticket-storypoints.component.scss']
})
export class TicketStorypointsComponent {
    @Input() points: number;
    @Output() pointsChange: EventEmitter<number> = new EventEmitter<number>();
}
