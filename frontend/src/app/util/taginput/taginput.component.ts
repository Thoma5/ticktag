import {
  Component, Input, OnChanges, Output, EventEmitter, SimpleChanges
} from '@angular/core';
import { findValue } from '../listmaputils';
import * as imm from 'immutable';

export interface Tag {
  id: string;
  name: string;
  color: string;
  order: number;
}

/**
 * Tag Input Element.
 */
@Component({
  selector: 'tt-taginput',
  templateUrl: './taginput.component.html',
  styleUrls: ['./taginput.component.scss'],
})
export class TaginputComponent implements OnChanges {
  @Input() allTags: imm.Map<string, Tag>;

  // Array of `Tag.id` values of `allTags`.
  @Input() tags: imm.List<string>;
  @Output() readonly tagsChange = new EventEmitter<string[]>();
  @Output() readonly tagAdd = new EventEmitter<string>();
  @Output() readonly tagRemove = new EventEmitter<string>();
  private sortedTags: imm.List<Tag>;

  private newTagName = '';
  private editing = false;

  ngOnChanges(changes: SimpleChanges): void {
    if ('tags' in changes || 'allTags' in changes) {
      this.sortedTags = this.tags
        .map(id => this.allTags.get(id))
        .filter(tag => !!tag)
        .sort((a, b) => (a.order < b.order) ? -1 : (a.order === b.order ? 0 : 1))
        .toList();
    }
  }

  onDeleteClick(tag: Tag) {
    let newTags = this.tags.slice();
    let index = newTags.indexOf(tag.id);
    if (index >= 0) {
      newTags.splice(index, 1);
      this.tagsChange.emit(newTags);
      this.tagRemove.emit(tag.id);
    }
  }

  onShowAdd() {
    this.editing = true;
  }

  onHideAdd() {
    this.editing = false;
  }

  onAdd() {
    let tag = findValue(this.allTags, t => t.name.toLowerCase() === this.newTagName.toLowerCase());
    if (tag) {
      let alreadyAdded = this.tags.indexOf(tag.id);
      if (alreadyAdded < 0) {
        let newTags = this.tags.slice();
        newTags.push(tag.id);
        this.tagsChange.emit(newTags);
        this.tagAdd.emit(tag.id);
      }
    }
    this.newTagName = '';
  }
}
