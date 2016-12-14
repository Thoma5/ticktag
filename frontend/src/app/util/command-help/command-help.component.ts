import { Component, Input } from '@angular/core';

@Component({
  selector: 'tt-command-help',
  templateUrl: './command-help.component.html',
  styleUrls: ['./command-help.component.scss']
})
export class CommandHelpComponent {
  @Input() noCommands = false;
}
