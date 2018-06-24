import {Component, Input, OnInit} from '@angular/core';
import {Presentation} from '../../../profile/shared/presentation.model';

@Component({
  selector: 'cf-presentation-metadata',
  templateUrl: './presentation-metadata.component.html',
  styleUrls: ['./presentation-metadata.component.scss']
})
export class PresentationMetadataComponent implements OnInit {
  @Input()
  presentation: Presentation;

  constructor() {
  }

  ngOnInit() {
  }

  isAccepted(presentation: Presentation) {
    return presentation.status === 'accepted';
  }

}
