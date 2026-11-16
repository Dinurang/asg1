import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

interface RoomType { name: string; pricePerPersonPerNight: number; rooms: number; maxAdults: number; }
interface Contract { id: number; hotelName: string; validFrom: string; validTo: string; markupPercent: number; roomTypes: RoomType[]; }
interface Result { hotelName: string; roomType: string; price: number; available: boolean; }

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  private readonly http = inject(HttpClient);
  private readonly fb = inject(FormBuilder);
  activeTab: 'search' | 'contracts' = 'search';
  contracts: Contract[] = [];
  results: Result[] = [];
  loading = false;
  message = '';
  rooms = this.fb.array([this.fb.group({ adults: [2, [Validators.required, Validators.min(1)]] })]);
  searchForm = this.fb.group({ checkIn: ['', Validators.required], nights: [1, [Validators.required, Validators.min(1)]] });
  contractForm = this.fb.group({
    hotelName: ['', Validators.required], validFrom: ['', Validators.required], validTo: ['', Validators.required],
    markupPercent: [15, [Validators.required, Validators.min(0.01)]],
    roomTypes: this.fb.array([this.fb.group({ name: ['Standard', Validators.required], pricePerPersonPerNight: [35, Validators.min(0.01)], rooms: [15, Validators.min(0)], maxAdults: [2, Validators.min(1)] })])
  });

  constructor() { this.loadContracts(); }
  get roomTypes(): FormArray { return this.contractForm.controls.roomTypes; }
  addRoom() { this.rooms.push(this.fb.group({ adults: [2, [Validators.required, Validators.min(1)]] })); }
  removeRoom(index: number) { if (this.rooms.length > 1) this.rooms.removeAt(index); }
  addRoomType() { this.roomTypes.push(this.fb.group({ name: ['', Validators.required], pricePerPersonPerNight: [0, Validators.min(0.01)], rooms: [0, Validators.min(0)], maxAdults: [2, Validators.min(1)] })); }
  removeRoomType(index: number) { if (this.roomTypes.length > 1) this.roomTypes.removeAt(index); }
  loadContracts() { this.http.get<Contract[]>('/api/contracts').subscribe({ next: data => this.contracts = data, error: () => this.message = 'Could not load contracts.' }); }
  search() {
    if (this.searchForm.invalid) return;
    this.loading = true; this.message = '';
    const value = this.searchForm.getRawValue();
    this.http.post<Result[]>('/api/search', { ...value, rooms: this.rooms.getRawValue() }).subscribe({
      next: data => { this.results = data; this.loading = false; },
      error: () => { this.message = 'Search failed. Check the entered criteria.'; this.loading = false; }
    });
  }
  saveContract() {
    if (this.contractForm.invalid) return;
    this.loading = true; this.message = '';
    this.http.post<Contract>('/api/contracts', this.contractForm.getRawValue()).subscribe({
      next: () => { this.message = 'Contract saved.'; this.loading = false; this.loadContracts(); this.contractForm.reset({ markupPercent: 15 }); },
      error: () => { this.message = 'Contract could not be saved.'; this.loading = false; }
    });
  }
}
