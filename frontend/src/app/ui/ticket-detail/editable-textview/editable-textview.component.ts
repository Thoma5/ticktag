import { Component, Input, Output, EventEmitter, ContentChild, AfterContentInit } from '@angular/core';

export interface TextviewReadComponent {
    text: string;
}
export interface TextviewEditComponent {
    text: string;
    textChange: EventEmitter<string>;
    abort: EventEmitter<void>;
    save: EventEmitter<void>;
}


@Component({
    selector: 'tt-editable-textview',
    templateUrl: './editable-textview.component.html',
    styleUrls: ['./editable-textview.component.scss']
})
export class EditableTextviewComponent implements AfterContentInit {
    @Input() text: string;
    _editing: boolean;
    _currentlyEditingText: string;
    currentlyEditingTextChange: EventEmitter<string> = new EventEmitter<string>();
    @Output() textChange: EventEmitter<string> = new EventEmitter<string>();
    @Output() editingChange: EventEmitter<boolean> = new EventEmitter<boolean>();
    @ContentChild('read') readComponent: TextviewReadComponent;
    @ContentChild('edit') editComponent: TextviewEditComponent;

    ngAfterContentInit() {
        if (this.readComponent != null) {
            this.readComponent.text = this.text;
            this.textChange
                .subscribe(() => {
                    this.readComponent.text = this.text;
                }, null, null);
        } else {
            console.log('Read component undefined');
        }
        if (this.editComponent != null) {
            this.editComponent.text = this.currentlyEditingText;
            this.currentlyEditingTextChange
                .subscribe((txt: string) => {
                    this.editComponent.text = txt;
                }, null, null);
            this.editComponent.textChange
                .subscribe((txt: string) => {
                    console.log(txt);
                    this.currentlyEditingText = txt;
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

    set currentlyEditingText(text: string) {
      this._currentlyEditingText = text;
      this.currentlyEditingTextChange.emit(this._currentlyEditingText);
    }

    get currentlyEditingText() {
      return this._currentlyEditingText;
    }

    updateEditing(editing: boolean): void {
        this._editing = editing;
        this.editingChange.emit(editing);
    }

    saveEdit(): void {
        this.text = this.currentlyEditingText;
        this.textChange.emit(this.text);
        this.updateEditing(false);
    }

    abortEdit(): void {
        if (this.currentlyEditingText !== this.text) {
            // TODO request confirmation due to unsaved changes?
        }
        this.updateEditing(false);
    }

    initEdit(): void {
        this.currentlyEditingText = this.text;
        this.editComponent.text = this.currentlyEditingText;
    }
}
