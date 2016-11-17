import { Component, Input } from '@angular/core';
import { UserResultJson } from '../../api';

// TODO remove this is a mock
export type AssignmentTagResultJson = {
  id: string,
  name: string,
  color: string,
};

@Component({
  selector: 'tt-assigned-user',
  templateUrl: './assigned-user.component.html',
  styleUrls: ['./assigned-user.component.scss']
})
export class AssignedUserComponent {
  @Input() user: UserResultJson;
  @Input() tags: AssignmentTagResultJson[];
}
