import { Component, Input, Output, OnInit, OnChanges, ElementRef, EventEmitter } from '@angular/core';

export interface Tag {
  name: string;
  color?: string;
  order: number;
}

@Component({
  selector: 'tt-taginput',
  templateUrl: './taginput.component.html',
  styleUrls: ['./taginput.component.scss'],
})
export class TaginputComponent {
  @Input() allTags: Tag[];

  @Input() tags: Tag[];
  @Output() readonly tagsChange = new EventEmitter<Tag[]>();
  get sortedTags(): Tag[] {
    let result = this.tags.slice();
    result.sort((a, b) => {
      return a.order - b.order;
    });
    return result;
  }

  newTagName: string;

  onDeleteClick(tag: Tag) {
    let newTags = this.tags.slice();
    let index = newTags.indexOf(tag);
    if (index >= 0) {
      newTags.splice(index, 1);
      this.tagsChange.next(newTags);
    }
  }

  onAddClick() {
    let tag = this.allTags.find(t => t.name.toLowerCase() === this.newTagName.toLowerCase());
    let alreadyAdded = this.tags.find(t => t.name.toLowerCase() === this.newTagName.toLowerCase());
    if (tag && !alreadyAdded) {
      let newTags = this.tags.slice();
      newTags.push(tag);
      this.tagsChange.next(newTags);
    }
    this.newTagName = '';
  }
}
