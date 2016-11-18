import { Component, Input, OnChanges, Output, EventEmitter } from '@angular/core';

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
  @Input() allTags: Tag[];
  private allTagsMap: {[id: string]: Tag};

  // Array of `Tag.id` values of `allTags`.
  @Input() tags: string[];
  @Output() readonly tagsChange = new EventEmitter<string[]>();
  private sortedTags: Tag[];

  private newTagName: string;

  ngOnChanges(): void {
    this.allTagsMap = {};
    for (let tag of this.allTags) {
      this.allTagsMap[tag.id] = tag;
    }

    this.sortedTags = this.tags.map(id => this.allTagsMap[id]);
    this.sortedTags.sort((a, b) => {
      return (a.order < b.order) ? -1 : (a.order === b.order ? 0 : 1);
    });
  }

  onDeleteClick(tag: Tag) {
    let newTags = this.tags.slice();
    let index = newTags.indexOf(tag.id);
    if (index >= 0) {
      newTags.splice(index, 1);
      this.tagsChange.next(newTags);
    }
  }

  onAddClick() {
    let tag = this.allTags.find(t => t.name.toLowerCase() === this.newTagName.toLowerCase());
    if (tag) {
      let alreadyAdded = this.tags.indexOf(tag.id);
      if (alreadyAdded < 0) {
        let newTags = this.tags.slice();
        newTags.push(tag.id);
        this.tagsChange.next(newTags);
      }
    }
    this.newTagName = '';
  }
}
