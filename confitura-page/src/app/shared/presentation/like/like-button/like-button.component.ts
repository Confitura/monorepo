import {Component, Input, OnInit} from '@angular/core';
import {Like, LikeService} from '../like.service';

@Component({
    selector: 'cf-like-button',
    templateUrl: './like-button.component.html',
    styleUrls: ['./like-button.component.css']
})
export class LikeButtonComponent implements OnInit {

    @Input()
    presentationId: string;

    like: Like = null;

    isLoading = false;

    constructor(private likeService: LikeService) {
    }

    ngOnInit() {
        this.likeService.getLikes()
            .subscribe(it => {
                this.like = it.find(like => like.presentationId === this.presentationId);
            });
    }

    doLike() {
        this.isLoading = true;
        this.likeService.like(this.presentationId)
            .subscribe(it => {
                this.isLoading = false;
                this.ngOnInit();
            });
    }

    doUnlike() {
        this.isLoading = true;
        this.likeService.unlike(this.like.id)
            .subscribe(it => {
                this.isLoading = false;
                this.ngOnInit();
            });
    }

}
