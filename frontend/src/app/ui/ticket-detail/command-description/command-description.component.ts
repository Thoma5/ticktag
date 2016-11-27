import { Component, Input } from '@angular/core';
import * as imm from 'immutable';
import { Cmd } from '../command-textview/grammar';

@Component({
  selector: 'tt-command-description',
  templateUrl: './command-description.component.html',
  styleUrls: ['./command-description.component.scss']
})
export class CommandDescriptionComponent {
  @Input() commands: imm.List<Cmd>;
}
