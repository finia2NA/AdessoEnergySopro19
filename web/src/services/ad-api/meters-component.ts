import AdessoEnergyApiComponent from './abstract-api-component';
import { Paging, MeterDTO } from '../../typings/dtos';

class MetersComponent extends AdessoEnergyApiComponent {
  public async getMetersForUser(id: string): Promise<Paging<MeterDTO>> {
    const result = await this.get(`/api/users/${id}/meters`);

    if (!result.ok) {
      throw new Error('A problem occurred with the request.');
    }

    return await result.json();
  }

  public async createNewMeter(meter: Partial<MeterDTO>): Promise<MeterDTO> {
    const result = await this.post('/api/meters', meter);

    if (!result.ok) {
      throw new Error('A problem occurred with the request.');
    }

    return await result.json();
  }
}

export default MetersComponent;
