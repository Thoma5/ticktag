import {
    Component, Input, Output, EventEmitter, ContentChild, AfterContentInit, OnChanges,
    SimpleChanges
} from '@angular/core';

export interface TextviewReadComponent<T> {
    visible: boolean;
    content: T;
}

export interface TextviewEditComponent<T> {
    visible: boolean;
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
export class EditableTextviewComponent<T> implements AfterContentInit, OnChanges {
    @Input() content: T;
    @Output() contentChange: EventEmitter<T> = new EventEmitter<T>();

    @ContentChild('read') private readonly readComponent: TextviewReadComponent<T>;
    @ContentChild('edit') private readonly editComponent: TextviewEditComponent<T>;

    private _editing = false;
    private get editing() {
        return this._editing;
    }
    private set editing(v: boolean) {
        this._editing = v;
        if (this.editComponent) {
            this.editComponent.visible = v;
        }
        if (this.readComponent) {
            this.readComponent.visible = !v;
        }
    }

    private _currentContent: T;
    private get currentContent() {
        return this._currentContent;
    }
    private set currentContent(v: T) {
        this._currentContent = v;
        if (this.editComponent) {
            this.editComponent.content = this.currentContent;
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        if ('content' in changes) {
            this.currentContent = this.content;
            this.editing = false;
            if (this.readComponent) {
                this.readComponent.content = this.content;
            }
        }
    }

    ngAfterContentInit() {
        this.editComponent.contentChange.subscribe((content: T) => {
            this.currentContent = content;
        });
        this.editComponent.save.subscribe(() => {
            this.contentChange.emit(this.currentContent);
            this.editing = false;
        });
        this.editComponent.abort.subscribe(() => {
            this.currentContent = this.content;
            this.editing = false;
        });
    }

    onEdit() {
        this.editing = true;
    }
}
