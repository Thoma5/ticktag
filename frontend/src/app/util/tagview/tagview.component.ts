import {
  Component, Input
} from '@angular/core';
import * as imm from 'immutable';


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


@Component({
  selector: 'tt-tagview',
  templateUrl: './tagview.component.html',
  styleUrls: ['./tagview.component.scss'],
})
export class TagViewComponent {
  @Input() tags: imm.List<TagRef>;

}
