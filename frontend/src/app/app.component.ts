import {Component} from '@angular/core';
import '../style/app.scss';

@Component({
  selector: 'tt-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  private title = 'TickTag';

  constructor() {
  }
}
