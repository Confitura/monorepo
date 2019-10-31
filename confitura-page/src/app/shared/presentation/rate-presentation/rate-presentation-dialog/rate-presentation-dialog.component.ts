import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {PresentationService} from '../../../../profile/shared/presentation.service';
import {Presentation} from '../../../../profile/shared/presentation.model';
import {RatingService} from '../rating.service';
import {Rate} from '../rating.model';

@Component({
  selector: 'cf-rate-presentation-dialog',
  templateUrl: './rate-presentation-dialog.component.html',
  styleUrls: ['./rate-presentation-dialog.component.css']
})
export class RatePresentationDialogComponent implements OnInit {

  rate: Rate;
  presentation: Presentation;

  constructor(
    private presentationService: PresentationService,
    private rateService: RatingService,
    public dialogRef: MatDialogRef<RatePresentationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {

    if (data.rate) {
      this.rate = new Rate(data.rate);
    } else {
      this.rate = this.rateService.getRate(data.presentationId) || new Rate();
    }
    presentationService.getOne(data.presentationId).subscribe(it => this.presentation = it);
  }

  ngOnInit() {
  }

}
