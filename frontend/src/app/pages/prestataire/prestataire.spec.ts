import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Prestataire } from './prestataire';

describe('Prestataire', () => {
  let component: Prestataire;
  let fixture: ComponentFixture<Prestataire>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Prestataire],
    }).compileComponents();

    fixture = TestBed.createComponent(Prestataire);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
