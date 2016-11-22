import { Component } from '@angular/core';

@Component({
  selector: 'tt-ticket-comment-input',
  templateUrl: './ticket-comment-input.component.html',
  styleUrls: ['./ticket-comment-input.component.scss']
})
export class TicketCommentInputComponent {
  private text: string;

  submit(): void {
    // TODO
    console.log('Submitted: ' + this.text);
  }
}
