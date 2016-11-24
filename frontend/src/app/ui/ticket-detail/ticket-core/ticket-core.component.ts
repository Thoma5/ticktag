import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TicketTagResultJson, UserResultJson } from '../../../api/';

@Component({
  selector: 'tt-ticket-core',
  templateUrl: './ticket-core.component.html',
  styleUrls: ['./ticket-core.component.scss']
})
export class TicketCoreComponent {
    @Input() open: boolean;
    // TODO no output
    @Input() title: string;
    @Output() titleChange = new EventEmitter<string>();
    @Input() description: string;
    @Output() descriptionChange = new EventEmitter<string>();
    @Input() storypoints: number;
    @Output() storypointsChange = new EventEmitter<number>();
    @Input() tagIds: string[];
    @Output() tagAdd = new EventEmitter<string>();
    @Output() tagRemove = new EventEmitter<string>();
    @Input() initialEstimatedTime: number;
    // TODO no output
    @Input() currentEstimatedTime: number;
    // TODO no output
    @Input() dueDate: number;
    // TODO no output

    // Readonly
    @Input() number: number;
    @Input() creator: UserResultJson;
    @Input() createTime: number;
    @Input() allTags: TicketTagResultJson[];

    editingTitle: boolean = false;
    editingDescription: boolean = false;
}
