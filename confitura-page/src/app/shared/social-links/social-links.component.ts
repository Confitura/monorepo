import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'cf-social-links',
  templateUrl: './social-links.component.html',
  styleUrls: ['./social-links.component.scss']
})
export class SocialLinksComponent implements OnInit {
  @Input()
  public twitter: string;
  @Input()
  public github: string;
  @Input()
  public www: string;

  constructor() {
  }

  ngOnInit() {
  }

}
