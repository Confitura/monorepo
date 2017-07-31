import {Component, OnDestroy} from "@angular/core";
import {V4pService} from "./v4p.service";
import {Vote} from "./vote.model";
import {Presentation} from "../../profile/shared/presentation.model";
import * as _ from "lodash";
import {PersonModalService} from "../../persons/person-modal/person-modal.service";
import {User} from "../profile/user.model";
import "./v4p.component.scss";
import {Router} from "@angular/router";

// import {Hotkey, HotkeysService} from "angular2-hotkeys";
@Component({
    templateUrl: "./v4p.component.html",
    host: {}
})
export class V4pComponent implements OnDestroy {
    ngOnDestroy(): void {
        // this.hotkeys.reset();
    }

    votes: Vote[];
    presentation: Presentation;
    currentIdx = 0;
    short = true;
    loading = false;

    constructor(private  service: V4pService,
                private modal: PersonModalService,
                private router: Router,
                // private hotkeys: HotkeysService
    ) {
        let token = localStorage.getItem("v4p-token");
        if (token == null) {
            router.navigate(["/v4p"])
        } else {
            service.start(token).subscribe((votes) => {
                    this.votes = _.sortBy(votes, "order");
                    this.currentIdx = this.votes.filter(it => it.rate != null).length;
                    if (this.currentIdx == this.votes.length) {
                        this.done();
                    } else {
                        this.loadPresentation();
                    }
                }
            );
        }
        // hotkeys
        //     .add(
        //         [
        //             new Hotkey("right", (): boolean => {
        //                 this.right();
        //                 modal.close();
        //                 return false;
        //             }, [], "Next presentation"),
        //             new Hotkey("left", (): boolean => {
        //                 this.left();
        //                 modal.close();
        //                 return false;
        //             }, [], "Previous presentation"),
        //             new Hotkey("up", (): boolean => {
        //                 this.up();
        //                 modal.close();
        //                 return false;
        //             }, [], "Vote Up!"),
        //             new Hotkey("down", (): boolean => {
        //                 this.down();
        //                 modal.close();
        //                 return false;
        //             }, [], "Vote Down!"),
        //             new Hotkey("b", (): boolean => {
        //                 this.bio();
        //                 return false;
        //             }, [], "Biography of the presenter"),
        //             new Hotkey("d", (): boolean => {
        //                 this.info();
        //                 modal.close();
        //                 return false;
        //             }, [], "Toggle between short and full description"),
        //             new Hotkey("esc", (): boolean => {
        //                 modal.close();
        //                 return false;
        //             }, [], "Close presenter's modal")
        //         ]
        //     )
    }


    show(speaker: User) {
        this.modal.showFor(speaker);
    }

    bio() {
        this.show(this.presentation.speaker);
    }


    up() {
        let rate = this.currentVote().rate;
        if (rate == null) {
            this.currentVote().rate = 0;
        }
        if (this.currentVote().rate < 1) {
            this.currentVote().rate++;
        }
    }

    down() {
        let rate = this.currentVote().rate;
        if (rate == null) {
            this.currentVote().rate = 0;
        }
        if (this.currentVote().rate > -1) {
            this.currentVote().rate--;
        }
    }

    left() {
        if (this.currentIdx > 0) {
            this.service.save(this.currentVote()).subscribe((response) => {
                this.currentIdx--;
                this.loadPresentation();
            });
        }

    }

    right() {
        if (this.currentIdx + 1 < this.votes.length) {
            this.service.save(this.currentVote()).subscribe((response) => {
                this.currentIdx++;
                this.loadPresentation();
            });
        } else {
            this.done()
        }
    }

    done() {
        this.service.save(this.currentVote()).subscribe((response) => {
            this.router.navigate(["v4p/end"]);
        });
    }

    info() {
        this.short = !this.short;
    }


    hasRate(rate: number) {
        return this.rate() == rate;
    }

    private rate() {
        return this.currentVote().rate;
    }


    private loadPresentation() {
        this.loading = true;
        this.service.getPresentationFor(this.currentVote())
            .subscribe((presentation) => {
                this.presentation = presentation;
                this.loading = false;
                if (this.currentVote().rate == null) {
                    this.currentVote().rate = 0;
                }
            });
    }

    currentVote() {
        return this.votes[this.currentIdx];
    }

    setRate(rate: number) {
        this.currentVote().rate = rate;
    }

}
