import {Component, Input, OnInit} from '@angular/core';
import {PresentationService} from '../../shared/presentation.service';
import {RatingService} from '../../../shared/presentation/rate-presentation/rating.service';
import {Rate} from '../../../shared/presentation/rate-presentation/rating.model';

@Component({
  selector: 'cf-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {

  @Input() presentationId: string;
  rates: RateToDisplayModel[] = [];
  private stats: { TERRIBLE: number; BAD: number; IT_WAS_FINE: number; GREAT: number; AWESOME: number; total: number; avg: number };


  chart = {
    chartType: 'BarChart',
    dataTable: [],
    options: {
      'legend': 'none',
      'title': 'votes',
    },
  };

  constructor(private service: RatingService) {

  }

  ngOnInit() {
    this.service.getRates(this.presentationId)
      .subscribe(response => {
        const stats = {
          TERRIBLE: 0,
          BAD: 0,
          IT_WAS_FINE: 0,
          GREAT: 0,
          AWESOME: 0,
          total: 0,
          avg: 0
        };

        this.rates = response.map(rate => this.addInfo(rate));
        this.rates.forEach(it => {
          stats[it.rate.value]++;
          stats.total += it.meta.no;
        });
        stats.avg = stats.total / this.rates.length;
        console.log(stats);
        this.stats = stats;
        this.chart.dataTable = [
          ['rate', 'votes'],
          ['Terrible', stats.TERRIBLE],
          ['Bad', stats.BAD],
          ['It was fine', stats.IT_WAS_FINE],
          ['Great', stats.GREAT],
          ['Awesome', stats.AWESOME]
        ];
        this.chart.options.title = 'Avarage: ' + this.stats.avg;

      });
  }

  private addInfo(rate: Rate): RateToDisplayModel {
    const meta = this.service.getRateMetaByName(rate.value);
    const stars = Array(meta.no).fill(1);
    return {
      rate: rate,
      meta: meta,
      stars: stars
    };
  }
}

interface RateToDisplayModel {
  rate: Rate;
  meta: any;
  stars: any[];
}
