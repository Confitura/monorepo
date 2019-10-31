import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'cf-photo',
  templateUrl: './photo.component.html',
  styleUrls: ['./photo.component.scss']
})
export class PhotoComponent implements OnInit {
  @Input()
  public src: string;

  constructor() {
  }

  ngOnInit() {
  }

}
