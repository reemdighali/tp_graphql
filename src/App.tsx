import React, { useState, useEffect } from 'react';
import './App.css';

// Types
type Compte = {
  id: number;
  solde: number;
  dateCreation: string;
  type: string;
};

type Transaction = {
  id: number;
  montant: number;
  type: string;
  date: string;
  compte: Compte;
};

// App component
const App: React.FC = () => {
  const [comptes, setComptes] = useState<Compte[]>([]);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [newCompte, setNewCompte] = useState({ solde: 0, type: 'COURANT' });
  const [transactionRequest, setTransactionRequest] = useState({
    montant: 0,
    type: 'DEPOT',
    compteId: 0,
  });

  useEffect(() => {
    fetch('http://localhost:8080/graphql', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        query: `{ allComptes { id solde type dateCreation } }`,
      }),
    })
      .then((response) => response.json())
      .then((data) => setComptes(data.data.allComptes));
  }, []);

  const handleCreateCompte = () => {
    const newCompteData = {
      solde: newCompte.solde,
      type: newCompte.type,
    };
    fetch('http://localhost:8080/graphql', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        query: `
          mutation {
            saveCompte(compte: {solde: ${newCompte.solde}, type: ${newCompte.type}}) {
              id solde type
            }
          }`,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        setComptes([...comptes, data.data.saveCompte]);
      });
  };

  const handleDeleteCompte = (id: number) => {
    fetch('http://localhost:8080/graphql', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        query: `
          mutation {
            deleteCompte(id: ${id})
          }`,
      }),
    })
      .then((response) => response.json())
      .then(() => {
        setComptes(comptes.filter((compte) => compte.id !== id));
      });
  };

  const handleTransaction = () => {
    const { montant, type, compteId } = transactionRequest;
    fetch('http://localhost:8080/graphql', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        query: `
          mutation {
            addTransaction(transactionRequest: {montant: ${montant}, type: ${type}, compteId: ${compteId}}) {
              id montant type date
            }
          }`,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        // Update transactions list
        setTransactions([...transactions, data.data.addTransaction]);

        // Update account balance after transaction
        const updatedComptes = comptes.map((compte) =>
          compte.id === compteId
            ? { ...compte, solde: compte.solde + (type === 'DEPOT' ? montant : -montant) }
            : compte
        );
        setComptes(updatedComptes);

        // Optionally, you can refetch all comptes from the server to ensure consistency
        fetch('http://localhost:8080/graphql', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            query: `{ allComptes { id solde type dateCreation } }`,
          }),
        })
          .then((response) => response.json())
          .then((data) => setComptes(data.data.allComptes));
      });
  };

  return (
    <div className="App">
      <h1>Account Management</h1>

      <div className="account-actions">
        <div>
          <input
            type="number"
            placeholder="Balance"
            value={newCompte.solde}
            onChange={(e) => setNewCompte({ ...newCompte, solde: parseFloat(e.target.value) })}
          />
          <select
            value={newCompte.type}
            onChange={(e) => setNewCompte({ ...newCompte, type: e.target.value })}
          >
            <option value="COURANT">COURANT</option>
            <option value="EPARGNE">EPARGNE</option>
          </select>
          <button onClick={handleCreateCompte}>Create Account</button>
        </div>

        <div>
          <h2>Accounts</h2>
          <ul>
            {comptes.map((compte) => (
              <li key={compte.id}>
                {compte.type} Account | Balance: {compte.solde} |
                <button onClick={() => handleDeleteCompte(compte.id)}>Delete</button>
              </li>
            ))}
          </ul>
        </div>
      </div>

      <div className="transaction-actions">
        <h2>Make a Transaction</h2>
        <select
          value={transactionRequest.type}
          onChange={(e) => setTransactionRequest({ ...transactionRequest, type: e.target.value })}
        >
          <option value="DEPOT">Deposit</option>
          <option value="RETRAIT">Withdrawal</option>
        </select>
        <input
          type="number"
          placeholder="Amount"
          value={transactionRequest.montant}
          onChange={(e) => setTransactionRequest({ ...transactionRequest, montant: parseFloat(e.target.value) })}
        />
        <select
          value={transactionRequest.compteId}
          onChange={(e) => setTransactionRequest({ ...transactionRequest, compteId: parseInt(e.target.value) })}
        >
          {comptes.map((compte) => (
            <option key={compte.id} value={compte.id}>
              {compte.type} Account (ID: {compte.id})
            </option>
          ))}
        </select>
        <button onClick={handleTransaction}>Perform Transaction</button>
      </div>

      <div className="transactions">
        <h2>Transactions</h2>
        <ul>
          {transactions.map((transaction) => (
            <li key={transaction.id}>
              {transaction.type} of {transaction.montant} on {transaction.date}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default App;
