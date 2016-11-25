import { Component, Input, EventEmitter, Output } from '@angular/core';
import { UserResultJson, AssignmentTagResultJson } from '../../../api';
import { Tag } from '../../../util/taginput/taginput.component';

@Component({
  selector: 'tt-assigned-user',
  templateUrl: './assigned-user.component.html',
  styleUrls: ['./assigned-user.component.scss']
})
export class AssignedUserComponent {
  @Input() user: UserResultJson;
  @Input() tags: string[];
  @Input() allTags: AssignmentTagResultJson[];

  @Output() readonly tagAdd = new EventEmitter<string>();
  @Output() readonly tagRemove = new EventEmitter<string>();

  get allTagsWrapped(): Tag[] {
    return this.allTags.map((t, i) => ({
      id: t.id,
      color: t.color,
      name: t.name,
      order: i,
    }));
  }
}
