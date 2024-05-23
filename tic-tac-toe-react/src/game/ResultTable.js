import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { refreshSession, getNick } from '../services/authenticate';
import './ResultTable.css';

const ResultTable = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [nick, setNick] = useState('');

  useEffect(() => {
    refreshSession();
    setNick(getNick);
    axios.get('/result', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
      .then(response => {
        setData(response.data);
        setLoading(false);
      })
      .catch(error => {
        setError(error);
        setLoading(false);
      });
  }, []);

  const getRowStyle = (moves, circlePlayer, crossPlayer) => {
    if (
      (nick === circlePlayer && moves.includes('o')) ||
      (nick === crossPlayer && moves.includes('x'))
    ) {
      return { backgroundColor: 'green', color: 'white' };
    } 
    else if (
      (nick === circlePlayer && moves.includes('x')) ||
      (nick === crossPlayer && moves.includes('o'))
    ) {
      return { backgroundColor: 'red', color: 'white' };
    } 
    else if (moves.includes('d')) {
      return { backgroundColor: 'white', color: 'black' };
    } 
    else {
      return {};
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error loading data</div>;

  return (
    <div className="table-container">
      <table className="styled-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Circle Player</th>
            <th>Cross Player</th>
            <th>Moves</th>
            <th>Start Time</th>
            <th>End Time</th>
          </tr>
        </thead>
        <tbody>
          {data.map((row) => (
            <tr key={row.id} style={getRowStyle(row.moves, row.circlePlayer, row.crossPlayer)}>
              <td>{row.id}</td>
              <td>{row.circlePlayer}</td>
              <td>{row.crossPlayer}</td>
              <td>{row.moves}</td>
              <td>{new Date(row.startTime).toLocaleString()}</td>
              <td>{new Date(row.endTime).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ResultTable;
