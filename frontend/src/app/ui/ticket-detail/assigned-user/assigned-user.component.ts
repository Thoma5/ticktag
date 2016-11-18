import { Component, Input, OnInit, ElementRef } from '@angular/core';
import { UserResultJson } from '../../../api';

// TODO remove this is a mock
export type AssignmentTagResultJson = {
  id: string,
  name: string,
  color: string,
  order: number,
};

@Component({
  selector: 'tt-assigned-user',
  templateUrl: './assigned-user.component.html',
  styleUrls: ['./assigned-user.component.scss']
})
export class AssignedUserComponent implements OnInit {
  @Input() user: UserResultJson;
  @Input() tags: string[];
  @Input() allTags: AssignmentTagResultJson[];

  constructor(private element: ElementRef) {
  }

  ngOnInit(): void {
  }
}
