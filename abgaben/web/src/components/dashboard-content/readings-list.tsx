import React from 'react';
import styles from './readings-list.module.scss';
import { Reading } from '../../typings/provider-data-interfaces';
import EditIcon from 'mdi-react/EditIcon';

interface ReadingList {
  readings: Reading[] | null;
  canEdit?: boolean;
  onEditClick?: (id: string, value: string) => void;
  unit: 'kWh' | 'm³';
}

const ReadingList: React.FC<ReadingList> = ({
  readings,
  canEdit,
  onEditClick,
  unit
}) => {
  if (!readings) return null;

  return (
    <div className={styles.ofProtection}>
      <table className={styles.table}>
        <thead>
          <tr>
            <th className={styles.headText}>Erfasst am</th>
            <th className={styles.headText}>Wert in {unit}</th>
            {canEdit && <th></th>}
          </tr>
        </thead>
        <tbody>
          {readings.map(reading => (
            <tr className={styles.row} key={reading.id}>
              <td className={styles.tableText}>
                {reading.createdAt.toLocaleDateString()}
              </td>
              <td className={styles.tableText}>{reading.value}</td>
              {canEdit && onEditClick && (
                <td className={styles.padding}>
                  <button
                    className={styles.button}
                    title="Edit reading"
                    onClick={onEditClick.bind(
                      undefined,
                      reading.id,
                      reading.value
                    )}
                  >
                    <EditIcon />
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ReadingList;
