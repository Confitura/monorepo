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

  toggleLike() {
    if (this.like) {
      this.doUnlike();
    } else {
      this.doLike();
    }
  }

  private doLike() {
    this.isLoading = true;
    return this.likeService.like(this.presentationId)
      .subscribe(() => {
        this.isLoading = false;
        this.ngOnInit();
      });
  }

  private doUnlike() {
    this.likeService.unlike(this.like.id)
      .subscribe(() => {
        this.isLoading = false;
        this.like = null;
      });
  }

}
