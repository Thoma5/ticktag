import { Component } from '@angular/core';

// TODO remove this, it is only to show that and how dragula works
@Component({
  selector: 'tt-dragula-sample',
  template: `
  <div>
    <div class='wrapper'>
      <div class='dragula-container' [dragula]='"first-bag"'>
        <div>You can move these elements between these two containers</div>
        <div>Moving them anywhere else isn't quite possible</div>
        <div>There's also the possibility of moving elements around in the same container, changing their position</div>
      </div>
      <div class='dragula-container' [dragula]='"first-bag"'>
        <div>This is the default use case. You only need to specify the containers you want to use</div>
        <div>More interactive use cases lie ahead</div>
        <div>Make sure to check out the <a href='https://github.com/bevacqua/dragula#readme'>documentation on GitHub!</a></div>
      </div>
    </div>
  </div>
  `,
  styles: [`
    .wrapper {
      display: table;
    }
    .dragula-container {
      display: table-cell;
      background-color: rgba(255, 255, 255, 0.2);
      width: 50%;
    }
    .dragula-container:nth-child(odd) {
      background-color: rgba(0, 0, 0, 0.2);
    }
    .dragula-container div,
    .gu-mirror {
      margin: 10px;
      padding: 10px;
      background-color: rgba(0, 0, 0, 0.2);
      transition: opacity 0.4s ease-in-out;
    }
    .dragula-container div {
      cursor: move;
      cursor: grab;
      cursor: -moz-grab;
      cursor: -webkit-grab;
    }
    .gu-mirror {
      cursor: grabbing;
      cursor: -moz-grabbing;
      cursor: -webkit-grabbing;
    }
    `]
})
export class DragulaSampleComponent {
}
