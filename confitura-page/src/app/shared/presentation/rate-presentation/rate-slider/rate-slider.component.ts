import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Rate} from '../rating.model';
import {RatingService} from '../rating.service';

@Component({
  selector: 'cf-rate-slider',
  templateUrl: './rate-slider.component.html',
  styleUrls: ['./rate-slider.component.css']
})
export class RateSliderComponent implements OnInit {

  @Input() rate: Rate = new Rate();
  @Output() rateChanged: EventEmitter<Rate> = new EventEmitter();

  sliderValue = -1;
  meta: { no: number; name: string; description: string } = {no: -1, name: null, description: ''};

  constructor(private service: RatingService) {
  }

  ngOnInit() {
    if (this.rate) {
      this.meta = this.service.getRateMetaByName(this.rate.value);
      this.sliderValue = this.meta.no;
    }
  }


  setValue(v) {
    const meta = this.service.getRateMetaByNumericValue(v.value);
    this.meta = meta;
    this.rate.value = meta.name;
    this.rateChanged.emit(this.rate);
  }
}
