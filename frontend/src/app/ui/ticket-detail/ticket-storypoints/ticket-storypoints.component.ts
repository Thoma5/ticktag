import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'tt-ticket-storypoints',
  templateUrl: './ticket-storypoints.component.html',
  styleUrls: ['./ticket-storypoints.component.scss']
})
export class TicketStorypointsComponent {
  @Input() points: number;
  currentEditingPoints: number;
  @Output() pointsChange: EventEmitter<number> = new EventEmitter<number>();
  editing: boolean = false;

  beginEdit(): void {
      this.editing = true;
      this.currentEditingPoints = this.points;
  }

  saveEdit(): void {
      this.editing = false;
      this.points = this.currentEditingPoints;
      this.pointsChange.emit(this.points);
  }

  abortEdit(): void {
      this.editing = false;
  }
}
