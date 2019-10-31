import {Component, OnInit, ViewChild, AfterViewInit} from '@angular/core';
import {MatSort, MatTableDataSource} from '@angular/material';
import {VoteStatsServiceService} from './vote-stats.service';
import {VoteStats} from './vote-stats.model';

@Component({
    templateUrl: './vote-list.component.html'
})
export class VoteListComponent implements OnInit, AfterViewInit {

    votes: VoteStats[];

    displayedColumns = [
        'title',
        'positiveVotes',
        'negativeVotes',
        'totalVotes',
        'rateOfPositive',
        'rateOfNegative'
    ];
    dataSource = new MatTableDataSource();

    @ViewChild(MatSort) sort: MatSort;

    constructor(private service: VoteStatsServiceService) {
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }

    ngOnInit(): void {
        this.service.getAll()
            .subscribe(votes => {
                this.votes = votes;
                this.dataSource.data = votes;
            });
    }

}
