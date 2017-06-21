import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {AgendaEntry} from "./agenda.model";
import {CustomHttp} from "../../../shared/custom-http.service";
import {Response} from "@angular/http";
import {Room} from "./room.model";
import {TimeSlot} from "./time-slot.model";
@Injectable()
export class AgendaService {


    constructor(private http: CustomHttp) {
    }

    getAgenda(){
        return Observable.zip(
            this.getRooms(),
            this.getTimeSlots(),
            this.getAll(),
            (rooms, slots, entries) => {
                let roomIdToIndex = this.idToIndex(rooms);
                let slotIdToIndex = this.idToIndex(slots);
                let matrix = this.createEmptyMatrix(slots, slotIdToIndex, rooms);

                for (let entry of entries) {
                    let slotIndex = slotIdToIndex[entry.timeSlotId];
                    if (entry.roomId == null) {
                        if (matrix[slotIndex][0] == null) {
                            matrix[slotIndex][0] = entry
                        } else {
                            console.warn("conflict in agenda. Two entries in same slot");
                        }
                    } else {
                        let roomIndex = roomIdToIndex[entry.roomId];
                        matrix[slotIndex][roomIndex] = entry;
                    }
                }

                return {
                    rooms: rooms,
                    slots: slots,
                    agenda: matrix,
                }
            }
        )
    }


    private idToIndex(entries: any[]) {
        let roomIdToIndex = {};
        let index = 0;
        for (let entry of entries) {
            roomIdToIndex[entry.id] = index++;
        }
        return roomIdToIndex;
    }

    private createEmptyMatrix(slots: any, slotIdToIndex: any, rooms: any) {
        let matrix = new Array(slots.length);
        for (let slot of slots) {
            let slotIndex = slotIdToIndex[slot.id];
            if (slot.forAllRooms) {
                matrix[slotIndex] = new Array(1);
            } else {
                matrix[slotIndex] = new Array(rooms.length)
            }
        }
        return matrix;
    }
    getAll(): Observable<AgendaEntry[]> {
        return this.http.get('/agenda')
            .map((response: Response) => response.json()["_embedded"]["agendaEntries"] as AgendaEntry[]);
    }

    addEntry(param: { timeSlot: string; room: string; label?: string , presentation?: string}) {
        return this.http.post(`/agenda/`, param);
    }

    getRooms(): Observable<Room[]> {
        return this.http.get('/rooms')
            .map((response: Response) => response.json()["_embedded"]["rooms"] as Room[]);
    }
    getTimeSlots(): Observable<TimeSlot[]> {
        return this.http.get('/time-slots')
            .map((response: Response) => response.json()["_embedded"]["timeSlots"] as TimeSlot[]);
    }

    save(participant: AgendaEntry) {
        return this.http.post(`/agenda/${participant.id}`, participant);
    }

    removeEntry(id: string) {
        return this.http.remove(`/agenda/${id}`);
    }

    addRoom(param: { label: string; displayOrder: number }) {
        return this.http.post(`/rooms/`, param);
    }

    addTimeSlot(param: { label: string; displayOrder: number, forAllRooms: boolean }) {
        return this.http.post(`/time-slots/`, param);
    }

    updateRoom(room: Room) {
        return this.http.post(`/rooms`, room);
    }

    updateTimeSlot(timeslot: TimeSlot) {
        return this.http.post(`/time-slots/`, timeslot);
    }

    removeRoom(id: string) {
        return this.http.remove(`/rooms/${id}`);
    }

    removeTimeSlot(id: string) {
        return this.http.remove(`/time-slots/${id}`);
    }
}
