import { Component, Input, Output, EventEmitter, ContentChild, AfterContentInit } from '@angular/core';

export interface TextviewReadComponent<T> {
    content: T;
}
export interface TextviewEditComponent<T> {
    active: boolean;
    content: T;
    contentChange: EventEmitter<T>;
    abort: EventEmitter<void>;
    save: EventEmitter<void>;
}


@Component({
    selector: 'tt-edit-textview',
    templateUrl: './edit-textview.component.html',
    styleUrls: ['./edit-textview.component.scss']
})
export class EditableTextviewComponent<T> implements AfterContentInit {
    @Input() content: T;
    _editing: boolean;
    _currentlyEditingContent: T;
    currentlyEditingContentChange: EventEmitter<T> = new EventEmitter<T>();
    @Output() contentChange: EventEmitter<T> = new EventEmitter<T>();
    @Output() editingChange: EventEmitter<boolean> = new EventEmitter<boolean>();
    @ContentChild('read') readComponent: TextviewReadComponent<T>;
    @ContentChild('edit') editComponent: TextviewEditComponent<T>;

    ngAfterContentInit() {
        if (this.readComponent != null) {
            this.readComponent.content = this.content;
            this.contentChange
                .subscribe(() => {
                    this.readComponent.content = this.content;
                }, null, null);
        } else {
            console.log('Read component undefined');
        }
        if (this.editComponent != null) {
            this.editComponent.content = this.currentlyEditingContent;
            this.editComponent.active = this.editing;
            this.editingChange
                .subscribe((value: boolean) => {
                    this.editComponent.active = value;
                }, null, null);
            this.currentlyEditingContentChange
                .subscribe((val: T) => {
                    this.editComponent.content = val;
                }, null, null);
            this.editComponent.contentChange
                .subscribe((val: T) => {
                    this.currentlyEditingContent = val;
                }, null, null);
            this.editComponent.abort
                .subscribe(() => {
                    this.abortEdit();
                }, null, null);
            this.editComponent.save
                .subscribe(() => {
                    this.saveEdit();
                }, null, null);
        } else {
            console.log('Edit component undefined');
        }
    }

    @Input()
    set editing(editing: boolean) {
        if (editing) {
            this.initEdit();
        }
        this.updateEditing(editing);
    }

    get editing() {
        return this._editing;
    }

    set currentlyEditingContent(value: T) {
      this._currentlyEditingContent = value;
      this.currentlyEditingContentChange.emit(this._currentlyEditingContent);
    }

    get currentlyEditingContent() {
      return this._currentlyEditingContent;
    }

    updateEditing(editing: boolean): void {
        this._editing = editing;
        this.editingChange.emit(editing);
    }

    saveEdit(): void {
        this.content = this.currentlyEditingContent;
        this.contentChange.emit(this.content);
        this.editComponent.active = false;
        this.updateEditing(false);
    }

    abortEdit(): void {
        if (this.currentlyEditingContent !== this.content) {
            // TODO request confirmation due to unsaved changes?
        }
        this.updateEditing(false);
    }

    initEdit(): void {
        this.currentlyEditingContent = this.content;
        this.editComponent.content = this.currentlyEditingContent;
    }
}
