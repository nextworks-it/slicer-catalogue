import { DataSource } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { map } from 'rxjs/operators';
import { Observable, of as observableOf, merge } from 'rxjs';

// TODO: Replace this with your own data model type
export interface DescriptorsVsItem {
  name: string;
  id: number;
  service: string;
  parameters: string[];
}

// TODO: replace this with real data from your application
const EXAMPLE_DATA: DescriptorsVsItem[] = [
  {id: 9, name: 'AGV_small_factory', service: 'ASTI AGV control and automation', parameters: ['max_AGVs: 5', 'max_control_servers: 1', 'avg_data_rate_per_AGV: 1 Mbps']},
  {id: 10, name: 'AGV_medium_factory', service: 'ASTI AGV control and automation', parameters: ['max_AGVs: 10', 'max_control_servers: 1', 'avg_data_rate_per_AGV: 1 Mbps']},
  {id: 11, name: 'AGV_large_factory', service: 'ASTI AGV control and automation', parameters: ['max_AGVs: 20', 'max_control_servers: 2', 'avg_data_rate_per_AGV: 1 Mbps']},
  {id: 24, name: 'UHD_100_users', service: 'TRENITALIA Smart Transport with on-board media services', parameters: ['max_users: 100', 'video_quality: UHD']},
  {id: 25, name: 'UHD_50_users', service: 'TRENITALIA Smart Transport with on-board media services', parameters: ['max_users: 50', 'video_quality: UHD']},
  {id: 26, name: 'HD_500_users', service: 'TRENITALIA Smart Transport with on-board media services', parameters: ['max_users: 500', 'video_quality: HD']},
  {id: 38, name: 'High_density_district', service: 'Smart Turin: safety and environment monitoring for mobility service', parameters: ['max_sensors: 1000', 'coverage_area: 2 kmsq', 'max_latency_per_sensor: 20 ms', 'avg_polling_frequency: 1/s']},
  {id: 39, name: 'Medium_density_district', service: 'Smart Turin: safety and environment monitoring for mobility service', parameters: ['max_sensors: 500', 'coverage_area: 2 kmsq', 'max_latency_per_sensor: 20 ms', 'avg_polling_frequency: 1/s']},
  {id: 40, name: 'Low_density_district', service: 'Smart Turin: safety and environment monitoring for mobility service', parameters: ['max_sensors: 100', 'coverage_area: 2 kmsq', 'max_latency_per_sensor: 20 ms', 'avg_polling_frequency: 1/s']},
];

/**
 * Data source for the DescriptorsVs view. This class should
 * encapsulate all logic for fetching and manipulating the displayed data
 * (including sorting, pagination, and filtering).
 */
export class DescriptorsVsDataSource extends DataSource<DescriptorsVsItem> {
  data: DescriptorsVsItem[] = EXAMPLE_DATA;
  paginator: MatPaginator;
  sort: MatSort;

  constructor() {
    super();
  }

  /**
   * Connect this data source to the table. The table will only update when
   * the returned stream emits new items.
   * @returns A stream of the items to be rendered.
   */
  connect(): Observable<DescriptorsVsItem[]> {
    // Combine everything that affects the rendered data into one update
    // stream for the data-table to consume.
    const dataMutations = [
      observableOf(this.data),
      this.paginator.page,
      this.sort.sortChange
    ];

    return merge(...dataMutations).pipe(map(() => {
      return this.getPagedData(this.getSortedData([...this.data]));
    }));
  }

  /**
   *  Called when the table is being destroyed. Use this function, to clean up
   * any open connections or free any held resources that were set up during connect.
   */
  disconnect() {}

  /**
   * Paginate the data (client-side). If you're using server-side pagination,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getPagedData(data: DescriptorsVsItem[]) {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    return data.splice(startIndex, this.paginator.pageSize);
  }

  /**
   * Sort the data (client-side). If you're using server-side sorting,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getSortedData(data: DescriptorsVsItem[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'name': return compare(a.name, b.name, isAsc);
        case 'id': return compare(+a.id, +b.id, isAsc);
        default: return 0;
      }
    });
  }
}

/** Simple sort comparator for example ID/Name columns (for client-side sorting). */
function compare(a, b, isAsc) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
