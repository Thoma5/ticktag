import {
  Component, Input, OnChanges, Output, EventEmitter, SimpleChanges, ElementRef
} from '@angular/core';
import * as imm from 'immutable';

const Awesomplete = require('awesomplete/awesomplete');

export interface Tag {
  id: string;
  name: string;
  color: string;
  order: number;
  normalizedName: string;
}

export type TagRef =
  string |
  { id: string, transient: boolean };

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
  @Input() tags: imm.List<TagRef>;
  @Input() editable: boolean=true;
  @Output() readonly tagsChange = new EventEmitter<imm.List<TagRef>>();
  @Output() readonly tagAdd = new EventEmitter<string>();
  @Output() readonly tagRemove = new EventEmitter<string>();
  private sortedTags: imm.List<{ tag: Tag, transient: boolean }>;

  private editing = false;
  private lastEditedText = '';

  constructor(private elementRef: ElementRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ('tags' in changes || 'allTags' in changes) {
      this.sortedTags = this.tags
        .map(tag => ({
          tag: typeof tag === 'string' ? this.allTags.get(tag) : this.allTags.get(tag.id),
          transient: typeof tag !== 'string' && tag.transient,
        }))
        .filter(tag => !!tag.tag)
        .sort((a, b) => (a.tag.order < b.tag.order) ? -1 : (a.tag.order === b.tag.order ? 0 : 1))
        .toList();
    }
  }

  onDeleteClick(tag: Tag) {
    this.tagsChange.emit(this.tags.filter(tid => typeof tid === 'string' ? tid === tag.id : tid.id === tag.id).toList());
    this.tagRemove.emit(tag.id);
  }

  onShowAdd() {
    this.editing = true;
    window.setTimeout(() => {
      let input: HTMLInputElement = this.elementRef.nativeElement.querySelector('.add-form input');
      let result = new Awesomplete(input, {
        list: this.allTags.map(tag => tag.normalizedName).toArray(),
        minChars: 0,
        autoFirst: true,
      });
      input = result.input;
      input.value = this.lastEditedText;
      input.select();
      input.addEventListener('keydown', (ev: KeyboardEvent) => {
        if (ev.key === 'Enter') {
          this.onAdd(input.value);
          input.value = '';
        } else if (ev.key === 'Escape') {
          this.editing = false;
        }
      });
      input.addEventListener('blur', () => {
        this.editing = false;
      });
      input.addEventListener('input', () => {
        this.lastEditedText = input.value;
      });
      input.addEventListener('awesomplete-select', (ev: any) => {
        this.lastEditedText = ev.text;
      });
      result.open();
    });
  }

  onHideAdd() {
    this.editing = false;
  }

  onAdd(newTagName: string) {
    let tag = this.allTags.find(t => t.normalizedName.toLowerCase() === newTagName.toLowerCase());
    if (tag) {
      let alreadyAdded = this.tags.findIndex(t => typeof t === 'string' ? t === tag.id : t.id === tag.id);
      if (alreadyAdded < 0) {
        this.tagsChange.emit(this.tags.push(tag.id));
        this.tagAdd.emit(tag.id);
      }
    }
  }
}
