import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'cf-back-button',
  templateUrl: './back-button.component.html',
})
export class BackButtonComponent implements OnInit {
  back = false;

  constructor(private location: Location,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.back = this.route.snapshot.queryParams.back;
  }

  goBack() {
    this.location.back();
  }

}
