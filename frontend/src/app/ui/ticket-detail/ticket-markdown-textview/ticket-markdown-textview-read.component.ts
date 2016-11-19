import { Component, Input } from '@angular/core';
import { TextviewReadComponent } from '../editable-textview/editable-textview.component';

@Component({
    selector: 'tt-markdown-textview-read',
    template: '{{text}}',
})
export class MarkdownTextviewReadComponent implements TextviewReadComponent {
    @Input() text: string;
}
