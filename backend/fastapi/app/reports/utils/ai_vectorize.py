import os
import faiss
import numpy as np
from sentence_transformers import SentenceTransformer
import glob

# Load the multilingual SentenceTransformer model
model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")

# Define the directory containing your text files
directory_path = "../documents/debatabase"

# Get all text files in the directory
text_files = glob.glob(os.path.join(directory_path, "*.txt"))

# Initialize FAISS index (FlatL2) with the correct dimension (384)
index = faiss.IndexFlatL2(384)  # 384 is the dimension of the embeddings

# List to hold the embeddings
embeddings = []
file_names = []

# Read each text file, vectorize, and add to the FAISS index
for file_path in text_files:
    with open(file_path, 'r', encoding='utf-8') as f:
        text = f.read().strip()  # Read the content of the file

    # Encode the text using the multilingual model
    embedding = model.encode(text)
    
    # Ensure embedding is 2D
    embeddings.append(embedding)  # Add each embedding to the list
    file_names.append(file_path)  # Store the file path in `file_names`

# Convert embeddings list to numpy array and ensure it is 2D
embeddings = np.array(embeddings, dtype=np.float32)

# Now embeddings should be a 2D array (n_samples x n_features)
print(f"Embedding shape: {embeddings.shape}")  # Check shape to ensure it's 2D

# Add embeddings to the FAISS index
index.add(embeddings)

# Save the FAISS index to a file
faiss.write_index(index, "debate_statements.index")

print(f"Added {len(text_files)} files to the FAISS index.")
print(file_names)