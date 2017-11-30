import {Component} from '@angular/core';
import * as lozad from "lozad";

@Component({
  templateUrl: './home.component.html'
})
export class HomeComponent {

  constructor() {

  }

  ngOnInit(): void {
    console.log(lozad);
    const l = lozad();
    l.observe();
  }


}
